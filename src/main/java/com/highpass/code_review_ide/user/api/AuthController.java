package com.highpass.code_review_ide.user.api;

import com.highpass.code_review_ide.user.api.dto.request.LoginRequest;
import com.highpass.code_review_ide.user.api.dto.request.LogoutRequest;
import com.highpass.code_review_ide.user.api.dto.request.SignupRequest;
import com.highpass.code_review_ide.user.api.dto.request.TokenReissueRequest;
import com.highpass.code_review_ide.user.api.dto.response.LoginResponse;
import com.highpass.code_review_ide.user.api.dto.response.MeResponse;
import com.highpass.code_review_ide.user.api.dto.response.SignupResponse;
import com.highpass.code_review_ide.user.api.dto.response.TokenReissueResponse;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.application.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

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
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new MeResponse(user.getId(), user.getEmail(), user.getNickname()));
    }
}
