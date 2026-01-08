package com.highpass.code_review_ide.api.auth;

import com.highpass.code_review_ide.api.auth.dto.*;
import com.highpass.code_review_ide.domain.user.User;
import com.highpass.code_review_ide.domain.user.UserRepository;
import com.highpass.code_review_ide.security.JwtProvider;
import com.highpass.code_review_ide.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest req) {
        SignupResponse res = authService.signup(req);
        return ResponseEntity.created(URI.create("/users/" + res.id())).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest req) {
        authService.logout(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/token")
    public ResponseEntity<TokenReissueResponse> reissue(@Valid @RequestBody TokenReissueRequest req) {
        return ResponseEntity.ok(authService.reissue(req));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 필요합니다. (Bearer 토큰)");
        }

        String token = authorization.substring("Bearer ".length()).trim();
        Long userId = jwtProvider.getUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return ResponseEntity.ok(new MeResponse(user.getId(), user.getEmail(), user.getNickname()));
    }
}
