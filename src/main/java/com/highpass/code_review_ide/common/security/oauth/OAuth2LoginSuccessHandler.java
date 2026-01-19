package com.highpass.code_review_ide.common.security.oauth;

import com.highpass.code_review_ide.common.security.JwtProvider;
import com.highpass.code_review_ide.common.security.RefreshTokenCodec;
import com.highpass.code_review_ide.user.domain.RefreshToken;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.domain.dao.RefreshTokenRepository;
import com.highpass.code_review_ide.user.domain.dao.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenCodec refreshTokenCodec;

    @Value("${app.refresh.days}")
    private int refreshDays;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String login = (String) attributes.get("login");
        
        if (email == null) {
            email = login + "@github.com";
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. Access Token 생성
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());

        // 2. Refresh Token 생성 및 저장
        String refreshPlain = refreshTokenCodec.generatePlain();
        String refreshHash = refreshTokenCodec.hash(refreshPlain);

        RefreshToken rt = new RefreshToken(
                user,
                refreshHash,
                LocalDateTime.now().plusDays(refreshDays)
        );
        refreshTokenRepository.save(rt);

        // 3. 프론트엔드로 리다이렉트 (Access Token + Refresh Token)
        String targetUrl = UriComponentsBuilder.fromUriString("https://ide.sjm00.link/oauth/callback")
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshPlain)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        log.info("OAuth2 Login Success. Redirecting to: {}", targetUrl);
        response.sendRedirect(targetUrl);
    }
}
