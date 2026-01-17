package com.highpass.code_review_ide.chat.api;

import com.highpass.code_review_ide.chat.api.dto.response.ChatMessageResponse;
import com.highpass.code_review_ide.chat.application.ChatCommandService;
import com.highpass.code_review_ide.chat.application.ChatQueryService;
import com.highpass.code_review_ide.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatCommandService chatCommandService;
    private final ChatQueryService chatQueryService;

    @PostMapping("/rooms/{roomId}/participants")
    public ResponseEntity<Void> joinChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal User user) {
        chatCommandService.addParticipantToRoomChat(roomId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> getChatHistory(@PathVariable Long roomId, @AuthenticationPrincipal User user){
        List<ChatMessageResponse> chatMessageResponses = chatQueryService.getChatHistory(roomId, user);
        return ResponseEntity.ok().body(chatMessageResponses);
    }
}
