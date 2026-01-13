package com.highpass.code_review_ide.config.chat;

import com.highpass.code_review_ide.service.chat.ChatQueryService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final SecretKey key;
    private final ChatQueryService chatQueryService;

    public StompHandler(@Value("${app.jwt.secret}") String secret, final ChatQueryService chatQueryService) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.chatQueryService = chatQueryService;
    }

    /*@Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

            if (StompCommand.CONNECT == accessor.getCommand()) {
                log.info("connect 요청시 토큰 유효성 검증");
                String bearerToken = accessor.getFirstNativeHeader("Authorization");
                String jwtToken = bearerToken.substring(7);

                SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(jwtToken)
                        .getPayload();
                log.info("토큰 검증 완료");
            }
            if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
                log.info("subscribe 검증");
                String bearerToken = accessor.getFirstNativeHeader("Authorization");
                String jwtToken = bearerToken.substring(7);

                SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
                final Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(jwtToken)
                        .getPayload();
                String email = claims.getSubject();

                // 현재 요청된 경로에서 roomId 확인
                Long roomId = Long.parseLong(accessor.getDestination().split("/")[2]);
                if (!chatQueryService.isRoomParticipant(email, roomId)){
                    throw new AuthenticationServiceException("해당 room에 권한이 없습니다.");
                }

            }

            return message;
        }*/
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 1. 연결(CONNECT) 또는 구독(SUBSCRIBE) 요청 시 토큰 검증
        if (StompCommand.CONNECT == accessor.getCommand() || StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String jwtToken = extractToken(accessor);

            try {
                // 토큰 검증 및 클레임 추출
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(jwtToken)
                        .getPayload();

                // 2. SUBSCRIBE일 경우 상세 권한 체크
                if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
                    // 토큰에서 정보 추출
                    final long userId = Long.parseLong(claims.getSubject());
                    String email = claims.get("email", String.class);

                    // 목적지 주소에서 roomId 추출 (예: /topic/chat/room/1)
                    String destination = accessor.getDestination();
                    Long roomId = parseRoomId(destination);

                    // 유저가 해당 방의 참여자인지 확인 (email 또는 userId 사용)
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

    // 헤더에서 토큰 추출 유틸 메서드
    private String extractToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new AuthenticationServiceException("토큰이 존재하지 않습니다.");
    }

    // 경로에서 roomId 추출 유틸 메서드
    private Long parseRoomId(String destination) {
        try {
            return Long.parseLong(destination.split("/")[3]); // 경로 구조에 따라 인덱스 조정 필요
        } catch (Exception e) {
            throw new AuthenticationServiceException("잘못된 목적지 경로입니다.");
        }
    }
}
