package com.highpass.code_review_ide.chat.application;

import com.highpass.code_review_ide.chat.domain.ChatMessage;
import com.highpass.code_review_ide.chat.domain.ChatRoom;
import com.highpass.code_review_ide.chat.domain.ChatMessageRepository;
import com.highpass.code_review_ide.chat.domain.ChatParticipantRepository;
import com.highpass.code_review_ide.chat.domain.ChatRoomRepository;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.domain.UserRepository;
import com.highpass.code_review_ide.chat.api.dto.request.ChatMessageRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ChatCommandService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public void saveMessage(final Long roomId, final ChatMessageRequest chatMessageRequest) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Can not find room"));

        User sender = userRepository.findByEmail(chatMessageRequest.senderEmail())
                .orElseThrow(() -> new EntityNotFoundException("Can not find member"));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .content(chatMessageRequest.message())
                .build();
        chatMessageRepository.save(chatMessage);
    }
}
