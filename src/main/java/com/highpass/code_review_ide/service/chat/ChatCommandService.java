package com.highpass.code_review_ide.service.chat;

import com.highpass.code_review_ide.domain.chat.ChatMessage;
import com.highpass.code_review_ide.domain.chat.ChatRoom;
import com.highpass.code_review_ide.domain.chat.dao.ChatMessageRepository;
import com.highpass.code_review_ide.domain.chat.dao.ChatParticipantRepository;
import com.highpass.code_review_ide.domain.chat.dao.ChatRoomRepository;
import com.highpass.code_review_ide.domain.user.User;
import com.highpass.code_review_ide.domain.user.UserRepository;
import com.highpass.code_review_ide.service.chat.dto.ChatMessageRequest;
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
