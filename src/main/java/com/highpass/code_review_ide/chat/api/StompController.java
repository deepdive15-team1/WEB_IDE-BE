package com.highpass.code_review_ide.chat.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.highpass.code_review_ide.chat.application.ChatCommandService;
import com.highpass.code_review_ide.chat.api.dto.request.ChatMessageRequest;
import com.highpass.code_review_ide.chat.api.dto.response.ChatMessageResponse;
import com.highpass.code_review_ide.chat.domain.ChatMessage;
import com.highpass.code_review_ide.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatCommandService chatCommandService;

    @MessageMapping("/room/{roomId}")
    public void sendMessage(@DestinationVariable final Long roomId, final ChatMessageRequest chatMessageRequest,
                            @AuthenticationPrincipal User user) throws JsonProcessingException {
        final ChatMessage savedMessage = chatCommandService.saveMessage(roomId, chatMessageRequest, user);

        final ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .roomId(roomId)
                .message(chatMessageRequest.message())
                .senderName(chatMessageRequest.senderName())
                .sendTime(savedMessage.getCreatedTime())
                .build();
        messagingTemplate.convertAndSend("/subscribe/room/" + roomId, chatMessageResponse);
    }
}
