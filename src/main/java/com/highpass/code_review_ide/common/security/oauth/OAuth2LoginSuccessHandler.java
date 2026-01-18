package com.highpass.code_review_ide.common.security.oauth;

import com.highpass.code_review_ide.common.security.JwtProvider;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.domain.dao.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // CustomOAuth2UserService와 동일한 로직으로 이메일 추출
        String email = (String) attributes.get("email");
        String login = (String) attributes.get("login");
        
        if (email == null) {
            email = login + "@github.com";
        }

        // DB에서 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 액세스 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());

        // 프론트엔드로 리다이렉트
        // 나중에 프론트엔드 개발 서버 주소로 변경 해야함
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth/callback")
                .queryParam("token", accessToken)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        log.info("OAuth2 Login Success. Redirecting to: {}", targetUrl);
        response.sendRedirect(targetUrl);
    }
}
