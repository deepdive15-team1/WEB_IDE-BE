package com.highpass.code_review_ide.auth.application;

import com.highpass.code_review_ide.auth.api.dto.request.LoginRequest;
import com.highpass.code_review_ide.auth.api.dto.response.LoginResponse;
import com.highpass.code_review_ide.auth.api.dto.request.SignupRequest;
import com.highpass.code_review_ide.auth.api.dto.response.SignupResponse;
import com.highpass.code_review_ide.auth.api.dto.request.TokenReissueRequest;
import com.highpass.code_review_ide.auth.api.dto.response.TokenReissueResponse;
import com.highpass.code_review_ide.auth.api.dto.request.LogoutRequest;
import com.highpass.code_review_ide.auth.domain.token.RefreshToken;
import com.highpass.code_review_ide.auth.infrastructure.repository.RefreshTokenRepository;
import com.highpass.code_review_ide.auth.infrastructure.security.JwtProvider;
import com.highpass.code_review_ide.auth.infrastructure.security.RefreshTokenCodec;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenCodec refreshTokenCodec;

    @Value("${app.refresh.days}")
    private int refreshDays;

    @Transactional
    public SignupResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(req.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String passwordHash = passwordEncoder.encode(req.password());
        User saved = userRepository.save(new User(req.email(), req.nickname(), passwordHash));
        return new SignupResponse(saved.getId(), saved.getEmail(), saved.getNickname());
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String access = jwtProvider.createAccessToken(user.getId(), user.getEmail());

        String refreshPlain = refreshTokenCodec.generatePlain();
        String refreshHash = refreshTokenCodec.hash(refreshPlain);

        RefreshToken rt = new RefreshToken(
                user,
                refreshHash,
                LocalDateTime.now().plusDays(refreshDays)
        );
        refreshTokenRepository.save(rt);

        return new LoginResponse(access, refreshPlain);
    }

    @Transactional(readOnly = true)
    public TokenReissueResponse reissue(TokenReissueRequest req) {
        String refreshHash = refreshTokenCodec.hash(req.refreshToken());

        RefreshToken rt = refreshTokenRepository.findByTokenHash(refreshHash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token이 유효하지 않습니다."));

        if (rt.getRevokedAt() != null) {
            throw new IllegalArgumentException("이미 로그아웃된 토큰입니다.");
        }
        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료된 Refresh Token입니다.");
        }

        User user = rt.getUser();
        String newAccess = jwtProvider.createAccessToken(user.getId(), user.getEmail());

        return new TokenReissueResponse(newAccess);
    }

    @Transactional
    public void logout(LogoutRequest req) {
        String refreshHash = refreshTokenCodec.hash(req.refreshToken());

        RefreshToken rt = refreshTokenRepository.findByTokenHash(refreshHash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token이 유효하지 않습니다."));

        if (rt.getRevokedAt() == null) {
            rt.revoke(LocalDateTime.now());
        }
    }
}
