package com.highpass.code_review_ide.domain.chat.dao;

import com.highpass.code_review_ide.domain.chat.ChatParticipant;
import com.highpass.code_review_ide.domain.chat.ChatRoom;
import com.highpass.code_review_ide.domain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);

    List<ChatParticipant> findByUser(User user);

    Optional<ChatParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    long countByChatRoom(ChatRoom chatRoom);


}
