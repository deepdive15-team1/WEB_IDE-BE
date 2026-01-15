package com.highpass.code_review_ide.auth.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RefreshTokenCodec {

    private final String pepper;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenCodec(@Value("${app.refresh.pepper}") String pepper) {
        this.pepper = pepper;
    }

    // 클라이언트에 줄 "원문 refresh"
    public String generatePlain() {
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // DB에 저장할 "hash(refresh + pepper)"
    public String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest((plain + ":" + pepper).getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash refresh token", e);
        }
    }
}
