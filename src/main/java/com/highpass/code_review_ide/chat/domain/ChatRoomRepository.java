package com.highpass.code_review_ide.chat.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByIsGroupChat(String isGroupChat);
}
