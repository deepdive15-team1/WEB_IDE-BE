package com.highpass.code_review_ide.common.config.chat;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.highpass.code_review_ide.chat.application.ChatQueryService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final SecretKey key;
    private final ChatQueryService chatQueryService;

    public StompHandler(@Value("${app.jwt.secret}") String secret, final ChatQueryService chatQueryService) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.chatQueryService = chatQueryService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand() || StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String jwtToken = extractToken(accessor);

            try {
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(jwtToken)
                        .getPayload();

                if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
                    // 토큰에서 정보 추출
                    final long userId = Long.parseLong(claims.getSubject());
                    String email = claims.get("email", String.class);

                    String destination = accessor.getDestination();
                    Long roomId = parseRoomId(destination);

                    if (!chatQueryService.isRoomParticipant(userId, roomId)) {
                        throw new AuthenticationServiceException("해당 채팅방에 접근 권한이 없습니다.");
                    }
                    log.info("User {}의 방 {} 구독을 허용합니다.", email, roomId);
                }

            } catch (JwtException e) {
                log.error("토큰 검증 실패: {}", e.getMessage());
                throw new AuthenticationServiceException("유효하지 않은 토큰입니다.");
            }
        }
        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new AuthenticationServiceException("토큰이 존재하지 않습니다.");
    }

    private Long parseRoomId(String destination) {
        try {
            return Long.parseLong(destination.split("/")[3]); // todo: 경로 구조에 따라 인덱스 조정 필요
        } catch (Exception e) {
            throw new AuthenticationServiceException("잘못된 목적지 경로입니다.");
        }
    }
}
