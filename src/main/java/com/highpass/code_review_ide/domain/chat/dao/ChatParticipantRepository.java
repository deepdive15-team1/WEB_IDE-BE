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

    boolean existsByChatRoomAndMember(ChatRoom chatRoom, User user);

    List<ChatParticipant> findByMember(User user);

    Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, User user);

    long countByChatRoom(ChatRoom chatRoom);

    @Query("""
            SELECT cp1.chatRoom 
            FROM ChatParticipant cp1 
            JOIN ChatParticipant cp2 
                ON cp1.chatRoom.id = cp2.chatRoom.id 
            WHERE cp1.member.id = :memberId 
              AND cp2.member.id = :otherMemberId 
              AND cp1.chatRoom.isGroupChat = 'N'
            """)
    Optional<ChatRoom> findExistingPrivateRoom(@Param("memberId") Long memberId,
                                               @Param("otherMemberId") Long otherMemberId);
}
