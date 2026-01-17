package com.highpass.code_review_ide.chat.application;

import com.highpass.code_review_ide.chat.api.dto.response.ChatMessageResponse;
import com.highpass.code_review_ide.chat.domain.ChatMessage;
import com.highpass.code_review_ide.chat.domain.ChatParticipant;
import com.highpass.code_review_ide.chat.domain.ChatRoom;
import com.highpass.code_review_ide.chat.domain.dao.ChatMessageRepository;
import com.highpass.code_review_ide.chat.domain.dao.ChatParticipantRepository;
import com.highpass.code_review_ide.chat.domain.dao.ChatRoomRepository;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.domain.dao.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatQueryService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public boolean isRoomParticipant(final long userId, final Long roomId) {
        final ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Can not find room"));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can not find user"));

        return chatParticipantRepository.findByChatRoom(chatRoom).stream()
                .anyMatch(participant -> participant.getUser().equals(user));
    }

    public List<ChatMessageResponse> getChatHistory(final Long roomId, final User user) {
        final ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Can not find room"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        chatParticipants.stream()
                .filter(participant -> participant.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("본인이 속하지 않은 채팅방입니다."));

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);
        return chatMessages.stream()
                .map(chatMessage -> ChatMessageResponse.builder()
                        .roomId(roomId)
                        .message(chatMessage.getContent())
                        .senderName(chatMessage.getUser().getNickname())
                        .sendTime(chatMessage.getCreatedTime())
                        .build()
                )
                .toList();
    }
}
