package com.highpass.code_review_ide.chat.application;

import com.highpass.code_review_ide.chat.domain.ChatMessage;
import com.highpass.code_review_ide.chat.domain.ChatParticipant;
import com.highpass.code_review_ide.chat.domain.ChatRoom;
import com.highpass.code_review_ide.chat.domain.dao.ChatMessageRepository;
import com.highpass.code_review_ide.chat.domain.dao.ChatParticipantRepository;
import com.highpass.code_review_ide.chat.domain.dao.ChatRoomRepository;
import com.highpass.code_review_ide.chat.exception.ChatExceptionType;
import com.highpass.code_review_ide.chat.exception.ChatException;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.domain.dao.UserRepository;
import com.highpass.code_review_ide.chat.api.dto.request.ChatMessageRequest;
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

    public ChatMessage saveMessage(final Long roomId, final ChatMessageRequest chatMessageRequest, final User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHAT_ROOM_NOT_FOUND));

        User sender = userRepository.findById(user.getId())
                .orElseThrow(() -> new ChatException(ChatExceptionType.USER_NOT_FOUND));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .content(chatMessageRequest.message())
                .build();
        return chatMessageRepository.save(chatMessage);
    }

    public void addParticipantToRoomChat(final Long roomId, final User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatExceptionType.CHAT_ROOM_NOT_FOUND));

        boolean isParticipated = chatParticipantRepository.existsByChatRoomAndUser(chatRoom, user);
        if (!isParticipated) {
            chatParticipantRepository.save(ChatParticipant.builder()
                    .chatRoom(chatRoom)
                    .user(user)
                    .build());
        }
    }
}