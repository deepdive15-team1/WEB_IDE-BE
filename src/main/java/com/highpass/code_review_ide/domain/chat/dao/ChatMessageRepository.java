package com.highpass.code_review_ide.domain.chat.dao;


import com.highpass.code_review_ide.domain.chat.ChatMessage;
import com.highpass.code_review_ide.domain.chat.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByCreatedTimeAsc(ChatRoom chatRoom);
}
