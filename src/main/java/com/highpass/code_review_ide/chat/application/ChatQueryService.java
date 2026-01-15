package com.highpass.code_review_ide.chat.application;

import com.highpass.code_review_ide.chat.domain.ChatRoom;
import com.highpass.code_review_ide.chat.domain.ChatMessageRepository;
import com.highpass.code_review_ide.chat.domain.ChatParticipantRepository;
import com.highpass.code_review_ide.chat.domain.ChatRoomRepository;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.domain.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new EntityNotFoundException("Can not find room"));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can not find user"));

        return chatParticipantRepository.findByChatRoom(chatRoom).stream()
                .anyMatch(participant -> participant.getUser().equals(user));
    }
}
