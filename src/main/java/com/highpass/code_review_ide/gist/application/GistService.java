package com.highpass.code_review_ide.gist.application;

import com.highpass.code_review_ide.gist.dto.GistRequest;
import com.highpass.code_review_ide.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GistService {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Gist 생성 (외부 API 호출이므로 트랜잭션 전파를 분리하거나 트랜잭션 없이 실행 권장)
     * 여기서는 읽기 전용 트랜잭션으로 사용자 정보 조회 등을 보장하되,
     * 외부 호출 실패가 상위 트랜잭션에 영향을 주지 않도록 주의해야 함.
     * 하지만 호출부에서 try-catch로 감싸므로 여기서는 기본 설정 유지.
     */
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public String createGist(User user, GistRequest request) {
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
        
        // 파일 이름 설정 (언어에 따른 확장자 처리)
        String fileName = makeFileName(request.title(), request.language());
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

    private String makeFileName(String title, String language) {
        String safeTitle = (title == null || title.isBlank()) ? "code_snippet" : title.replaceAll("\\s+", "_");
        String extension = getExtensionByLanguage(language);
        // 이미 확장자가 있으면 그대로 사용
        if (safeTitle.toLowerCase().endsWith(extension)) {
            return safeTitle;
        }
        return safeTitle + extension;
    }

    private String getExtensionByLanguage(String language) {
        if (language == null) return ".txt";
        return switch (language.toLowerCase()) {
            case "java" -> ".java";
            case "python" -> ".py";
            case "javascript", "js" -> ".js";
            case "typescript", "ts" -> ".ts";
            case "c" -> ".c";
            case "cpp", "c++" -> ".cpp";
            case "html" -> ".html";
            case "css" -> ".css";
            case "go" -> ".go";
            case "kotlin" -> ".kt";
            case "swift" -> ".swift";
            default -> ".txt";
        };
    }
}
