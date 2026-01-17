package com.highpass.code_review_ide.chat.application;

import com.highpass.code_review_ide.chat.api.dto.response.ChatMessageResponse;
import com.highpass.code_review_ide.chat.domain.ChatMessage;
import com.highpass.code_review_ide.chat.domain.ChatRoom;
import com.highpass.code_review_ide.chat.domain.dao.ChatMessageRepository;
import com.highpass.code_review_ide.chat.domain.dao.ChatParticipantRepository;
import com.highpass.code_review_ide.chat.domain.dao.ChatRoomRepository;
import com.highpass.code_review_ide.chat.exception.ChatExceptionType;
import com.highpass.code_review_ide.chat.exception.ChatException;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.domain.dao.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHAT_ROOM_NOT_FOUND));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.USER_NOT_FOUND));

        return chatParticipantRepository.existsByChatRoomAndUser(chatRoom, user);
    }

    public List<ChatMessageResponse> getChatHistory(final Long roomId, final User user) {
        final ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHAT_ROOM_NOT_FOUND));

        boolean isParticipant = chatParticipantRepository.existsByChatRoomAndUser(chatRoom, user);
        if (!isParticipant) {
            throw new ChatException(ChatExceptionType.NOT_PARTICIPANT);
        }

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