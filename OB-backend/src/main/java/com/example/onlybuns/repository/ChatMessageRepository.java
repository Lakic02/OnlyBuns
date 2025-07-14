package com.example.onlybuns.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.onlybuns.domain.ChatMessage;
import org.springframework.data.domain.*;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{
    // Metoda za pronalaženje svih poruka za određeni grupni čet (chatRoomId)
    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);
    List<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);

    // Pribavljanje poslednjih N poruka za određeni grupni čet
    @Query(value = "SELECT m FROM ChatMessage m WHERE m.chatRoomId = :chatRoomId ORDER BY m.timestamp DESC")
    List<ChatMessage> findTopNByChatRoomId(@Param("chatRoomId") Long chatRoomId, Pageable pageable);

    //@Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoomId = :chatRoomId AND cm.timestamp < :date ORDER BY cm.timestamp DESC")
    //List<ChatMessage> findMessagesBeforeDate(@Param("chatRoomId") Long chatRoomId, @Param("date") LocalDateTime date);
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoomId = :chatRoomId AND cm.timestamp < :date ORDER BY cm.timestamp DESC")
    List<ChatMessage> findMessagesBeforeDate(@Param("chatRoomId") Long chatRoomId, @Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoomId = :chatRoomId AND cm.timestamp > :date ORDER BY cm.timestamp ASC")
    List<ChatMessage> findMessagesAfterDate(@Param("chatRoomId") Long chatRoomId, @Param("date") LocalDateTime date);
            
    List<ChatMessage> findByChatRoomIdAndTimestampAfter(Long chatRoomId, LocalDateTime date, Pageable pageable);
}
