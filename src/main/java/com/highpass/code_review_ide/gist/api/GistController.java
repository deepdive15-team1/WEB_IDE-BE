package com.highpass.code_review_ide.gist.api;

import com.highpass.code_review_ide.gist.dto.GistRequest;
import com.highpass.code_review_ide.gist.application.GistService;
import com.highpass.code_review_ide.user.domain.User;
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
@RequestMapping("/gist")
@RequiredArgsConstructor
public class GistController {

    private final GistService gistService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createGist(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody GistRequest request
    ) {
        String gistUrl = gistService.createGist(user, request);
        return ResponseEntity.ok(Map.of("url", gistUrl));
    }
}
