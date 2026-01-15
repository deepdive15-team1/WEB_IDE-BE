package com.highpass.code_review_ide.chat.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.highpass.code_review_ide.chat.application.ChatCommandService;
import com.highpass.code_review_ide.chat.api.dto.request.ChatMessageRequest;
import com.highpass.code_review_ide.chat.api.dto.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatCommandService chatCommandService;

    @MessageMapping("/room/{roomId}")
    public void sendMessage(@DestinationVariable final Long roomId, final ChatMessageRequest chatMessageRequest) throws JsonProcessingException {
        chatCommandService.saveMessage(roomId, chatMessageRequest);
        final ChatMessageResponse chatMessageResponse = new ChatMessageResponse(roomId, chatMessageRequest.message(),
                chatMessageRequest.senderEmail());
        messagingTemplate.convertAndSend("/subscribe/room/" + roomId, chatMessageResponse);
    }
}
