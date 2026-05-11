package com.careconnect.repository;

import com.careconnect.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :u1 AND m.receiverId = :u2) OR " +
           "(m.senderId = :u2 AND m.receiverId = :u1) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("u1") Long u1, @Param("u2") Long u2);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiverId = :userId AND m.senderId = :senderId")
    void markReadBySender(@Param("userId") Long userId, @Param("senderId") Long senderId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :userId AND m.isRead = false")
    long countUnread(@Param("userId") Long userId);

    @Query("SELECT DISTINCT m.senderId FROM Message m WHERE m.receiverId = :userId AND m.isRead = false")
    List<Long> findSendersWithUnread(@Param("userId") Long userId);
}
