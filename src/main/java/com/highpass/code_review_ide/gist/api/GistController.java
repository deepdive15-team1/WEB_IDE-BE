package com.highpass.code_review_ide.gist.api;

import com.highpass.code_review_ide.gist.dto.GistRequest;
import com.highpass.code_review_ide.gist.application.GistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/gist") // URL 변경 반영
@RequiredArgsConstructor
public class GistController {

    private final GistService gistService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createGist(
            @AuthenticationPrincipal Long userId, // 필터에서 넣어준 userId를 바로 사용
            @Valid @RequestBody GistRequest request
    ) {
        String gistUrl = gistService.createGist(userId, request);
        return ResponseEntity.ok(Map.of("url", gistUrl));
    }
}
