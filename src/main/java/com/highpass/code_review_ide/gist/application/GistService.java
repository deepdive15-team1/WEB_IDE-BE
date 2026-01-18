package com.highpass.code_review_ide.gist.application;

import com.highpass.code_review_ide.gist.dto.GistRequest;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.domain.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GistService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional(readOnly = true)
    public String createGist(Long userId, GistRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String githubToken = user.getGithubAccessToken();
        if (githubToken == null) {
            throw new IllegalStateException("GitHub 연동이 필요합니다. 다시 로그인해주세요.");
        }

        // GitHub Gist API 요청 바디 생성
        Map<String, Object> body = new HashMap<>();
        body.put("description", request.description());
        body.put("public", true); // 공개 Gist로 생성

        Map<String, Object> files = new HashMap<>();
        Map<String, String> fileContent = new HashMap<>();
        fileContent.put("content", request.content());
        
        // 파일 이름이 없으면 기본값 설정
        String fileName = (request.title() == null || request.title().isBlank()) ? "code_snippet.txt" : request.title();
        files.put(fileName, fileContent);
        
        body.put("files", files);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.github.com/gists",
                    entity,
                    Map.class
            );

            // 생성된 Gist의 URL 반환
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("html_url")) {
                return (String) responseBody.get("html_url");
            } else {
                throw new RuntimeException("Gist 생성 실패: 응답에 URL이 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("GitHub API 호출 중 오류 발생: " + e.getMessage());
        }
    }
}
