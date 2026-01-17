package com.highpass.code_review_ide.chat.domain.dao;


import java.util.List;

import com.highpass.code_review_ide.chat.domain.ChatMessage;
import com.highpass.code_review_ide.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("""
            SELECT m\s
            FROM ChatMessage m\s
            JOIN FETCH m.user\s
            WHERE m.chatRoom = :chatRoom\s
            ORDER BY m.createdTime ASC
            """)
    List<ChatMessage> findByChatRoomOrderByCreatedTimeAsc(@Param("chatRoom") ChatRoom chatRoom);
}
