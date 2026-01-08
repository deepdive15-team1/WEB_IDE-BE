package com.highpass.code_review_ide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CodeReviewIdeApplication {
	public static void main(String[] args) {
		SpringApplication.run(CodeReviewIdeApplication.class, args);
	}
}
