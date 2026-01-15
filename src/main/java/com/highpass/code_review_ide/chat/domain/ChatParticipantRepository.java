package com.highpass.code_review_ide.chat.domain;

import com.highpass.code_review_ide.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);

    List<ChatParticipant> findByUser(User user);

    Optional<ChatParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    long countByChatRoom(ChatRoom chatRoom);


}
