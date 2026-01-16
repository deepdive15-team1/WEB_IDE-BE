package com.highpass.code_review_ide.chat.domain.dao;

import java.util.List;

import com.highpass.code_review_ide.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByIsGroupChat(String isGroupChat);
}
