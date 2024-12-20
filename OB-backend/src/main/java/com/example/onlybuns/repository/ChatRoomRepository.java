package com.example.onlybuns.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>{
        // Pronalazi sve sobe gde je korisnik ili admin ili član
        // @Query("SELECT cr FROM ChatRoom cr WHERE cr.admin = :account OR cr.id IN " +
        //         "(SELECT crm.chatRoom.id FROM ChatRoomMembership crm WHERE crm.account = :account)")
        // List<ChatRoom> findByAdminOrMember(@Param("account") Account account);
        @Query("SELECT cr FROM ChatRoom cr WHERE cr.admin.id = :accountId OR cr.id IN " +
                "(SELECT crm.chatRoom.id FROM ChatRoomMembership crm WHERE crm.account.id = :accountId)")
        List<ChatRoom> findChatRoomsForUser(@Param("accountId") Long accountId);
 
        // Proverava da li soba sadrži određenog člana
        @Query("SELECT CASE WHEN COUNT(crm) > 0 THEN true ELSE false END FROM ChatRoomMembership crm " +
                "WHERE crm.chatRoom.id = :chatRoomId AND crm.account.id = :memberId")
        boolean existsByChatRoomIdAndMemberId(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    //List<ChatRoom> findByAdminOrMembersContaining(Account admin, Account member);
    
    // @Modifying
    // @Query("INSERT INTO chat_room_members (chat_room_id, account_id) VALUES (:chatRoomId, :accountId)")
    // void addMemberToChatRoom(@Param("chatRoomId") Long chatRoomId, @Param("accountId") Long accountId);
}
