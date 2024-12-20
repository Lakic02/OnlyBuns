package com.example.onlybuns.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlybuns.domain.ChatRoomMembership;

public interface ChatRoomMembershipRepository extends JpaRepository<ChatRoomMembership, Long>{
  List<ChatRoomMembership> findByChatRoomId(Long chatRoomId);

  @Query("SELECT crm FROM ChatRoomMembership crm WHERE crm.chatRoom.id = :chatRoomId AND crm.account.id = :accountId")
  ChatRoomMembership findByChatRoomIdAndAccountId(@Param("chatRoomId") Long chatRoomId, @Param("accountId") Long accountId);
}
