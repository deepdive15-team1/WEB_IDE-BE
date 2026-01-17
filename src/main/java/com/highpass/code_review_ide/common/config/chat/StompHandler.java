package com.highpass.code_review_ide.common.config.chat;

import com.highpass.code_review_ide.chat.application.ChatQueryService;
import com.highpass.code_review_ide.chat.exception.ChatException;
import com.highpass.code_review_ide.common.security.JwtProvider;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.domain.dao.UserRepository;
import io.jsonwebtoken.JwtException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROOM_PATH_PREFIX = "room";

    private final JwtProvider jwtProvider;
    private final ChatQueryService chatQueryService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            throw new AuthenticationServiceException("헤더에 접근할 수 없습니다.");
        }

        final StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT == command) {
            handleConnect(accessor);
        } else if (StompCommand.SUBSCRIBE == command) {
            handleSubscribe(accessor);
        }

        return message;
    }

    private void handleConnect(final StompHeaderAccessor accessor) {
        final String jwtToken = extractToken(accessor);
        try {
            final Long userId = jwtProvider.getUserId(jwtToken);
            final User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthenticationServiceException("사용자를 찾을 수 없습니다."));

            final Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            accessor.setUser(auth);
        } catch (final JwtException e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
        }
    }

    private void handleSubscribe(final StompHeaderAccessor accessor) {
        final Authentication auth = (Authentication) accessor.getUser();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new AuthenticationServiceException("인증되지 않은 사용자입니다.");
        }

        final String destination = accessor.getDestination();
        final Long roomId = parseRoomId(destination);

        try {
            if (!chatQueryService.isRoomParticipant(user.getId(), roomId)) {
                throw new AuthenticationServiceException("해당 채팅방에 접근 권한이 없습니다.");
            }
        } catch (final ChatException e) {
            throw new AuthenticationServiceException("채팅방 접근 권한 확인 중 오류 발생: " + e.getMessage());
        }
        log.info("User(ID: {})의 방 {} 구독을 허용합니다.", user.getId(), roomId);
    }

    private String extractToken(final StompHeaderAccessor accessor) {
        final String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        throw new AuthenticationServiceException("토큰이 존재하지 않습니다.");
    }

    private Long parseRoomId(final String destination) {
        if (!StringUtils.hasText(destination)) {
            throw new AuthenticationServiceException("목적지가 존재하지 않습니다.");
        }

        try {
            final String[] parts = destination.split("/");
            if (parts.length < 4 || !ROOM_PATH_PREFIX.equals(parts[2])) {
                log.warn("잘못된 구독 경로 패턴: {}", destination);
                throw new AuthenticationServiceException("잘못된 목적지 경로입니다.");
            }
            return Long.parseLong(parts[3]);
        } catch (final NumberFormatException e) {
            throw new AuthenticationServiceException("Room ID 형식이 올바르지 않습니다.");
        }
    }
}