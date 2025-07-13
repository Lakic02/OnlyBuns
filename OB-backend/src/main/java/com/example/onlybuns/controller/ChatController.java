package com.example.onlybuns.controller;

import java.time.LocalDateTime;
import java.util.*;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.ChatMessage;
import com.example.onlybuns.domain.ChatRoom;
import com.example.onlybuns.service.AccountService;
import com.example.onlybuns.service.ChatService;

@RestController
@RequestMapping("/api/chat")
@PreAuthorize("hasAuthority('ROLE_REGISTERED')")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private AccountService accountService;

    // WebSocket za slanje poruka u realnom vremenu
    @MessageMapping("/chat.sendMessage/{chatRoomId}")
    @SendTo("/topic/chatRoom/{chatRoomId}")
    public ChatMessage sendMessage(@DestinationVariable Long chatRoomId, ChatMessage chatMessage) throws AccountNotFoundException {
        System.out.println("USLO JE U CHAT SLANJE PORUKEEEEEEEEEEEEEEEEEEEEEe");
        chatMessage.setTimestamp(LocalDateTime.now());
        chatService.saveMessage(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser/{chatRoomId}")
    @SendTo("/topic/chatRoom/{chatRoomId}")
    public ChatMessage addUserToChatRoom(@DestinationVariable Long chatRoomId, @RequestBody Map<String, Object> payload) throws AccountNotFoundException {
            // Pronađi korisnika po emailu
        // if(!accountService.existsByEmail(email)){
        //     System.out.println("NE VALJA");
        //     return;
        // }
        //System.out.println(email);
        //String email = (String) payload.get("email");
        String email = (String) payload.get("email");
        System.out.println(email);

        Account user = accountService.findByEmail(email);
        chatService.addUserToChatRoom(chatRoomId, user);
        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setContent(user.getUserName() + " has joined the chat");
        joinMessage.setSender(user);
        joinMessage.setTimestamp(LocalDateTime.now());
        joinMessage.setChatRoomId(chatRoomId);
        chatService.saveMessage(joinMessage);
        return joinMessage;
    }
    @MessageMapping("/chat.removeUser/{chatRoomId}")
    @SendTo("/topic/chatRoom/{chatRoomId}")
    public ChatMessage removeChatRoomMembership(@DestinationVariable Long chatRoomId, @RequestBody Map<String, Object> payload) throws AccountNotFoundException {
        String email = (String) payload.get("email");
        System.out.println(email);

        Account user = accountService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("user not found for the given email.");
        }
        //Account user = accountService.findById(userId)
          //  .orElseThrow(() -> new AccountNotFoundException("User not found with ID: " + userId));
        
        chatService.removeChatRoomMembership(chatRoomId, user.getId());

        ChatMessage removalMessage = new ChatMessage();
        removalMessage.setContent(user.getUserName() + " has been removed from the chat");
        removalMessage.setSender(user);
        removalMessage.setTimestamp(LocalDateTime.now());
        removalMessage.setChatRoomId(chatRoomId);
        chatService.saveMessage(removalMessage);
        return removalMessage;
    }


    @GetMapping("/getMessages/{chatRoomId}")
    public List<ChatMessage> getMessagesByChatRoom(@PathVariable Long chatRoomId) {
        return chatService.getMessagesByChatRoom(chatRoomId);
    }
    @GetMapping("/getMessagesForChatRoom/{chatRoomId}/{userId}")
    public Map<String, Object> getMessagesForChatRoom(@PathVariable Long chatRoomId, @PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
    
        // Check if the user is an admin
        boolean isAdmin = chatService.isUserAdmin(chatRoomId, userId);
        
        if (isAdmin) {
            // If the user is an admin, return all messages (both before and after joining)
            List<ChatMessage> allMessages = chatService.getMessagesByChatRoom(chatRoomId); // Method to get all messages
            response.put("allMessages", allMessages);
        } else {
            // If the user is not an admin, fetch messages based on their join date
            List<ChatMessage> lastMessages = chatService.getMessagesBeforeJoining(chatRoomId, userId);
            List<ChatMessage> newMessages = chatService.getLastMessagesAfterJoining(chatRoomId, userId);
            
            response.put("lastMessages", lastMessages);
            response.put("newMessages", newMessages);
        }

        return response;
    }


    // Prikazuje poslednjih 10 poruka kada novi korisnik uđe u grupni čet
    // @GetMapping("/getLastMessages/{chatRoomId}")
    // public List<ChatMessage> getLastMessagesByChatRoom(@PathVariable Long chatRoomId) {
    //     return chatService.getLastNMessagesByChatRoom(chatRoomId, 10);
    // }

    // // Dohvati poruke koje su poslate pre nego što je korisnik postao član
    // @GetMapping("/getMessagesBeforeJoining/{chatRoomId}/{accountId}")
    // public List<ChatMessage> getMessagesBeforeJoining(@PathVariable Long chatRoomId, @PathVariable Long accountId) {
    //     return chatService.getMessagesBeforeJoining(chatRoomId, accountId);
    // }

    // // Dohvati poslednjih 10 poruka nakon što je korisnik postao član
    // @GetMapping("/getLastMessagesAfterJoining/{chatRoomId}/{accountId}")
    // public List<ChatMessage> getLastMessagesAfterJoining(@PathVariable Long chatRoomId, @PathVariable Long accountId) {
    //     return chatService.getLastMessagesAfterJoining(chatRoomId, accountId, 10); // Limituje na 10 poruka
    // }


    // Kreiranje nove sobe za čet sa zadatim adminom
    @PostMapping("/createRoom/{roomName}/{adminId}")
    public ChatRoom createChatRoom(@PathVariable String roomName, @PathVariable Long adminId) {
        return chatService.createChatRoom(roomName, adminId);
    }

    // @GetMapping("/getChatRooms/{accountId}")
    // public List<ChatRoom> getChatRoomsForUser(@PathVariable Long accountId) {
    //     // Pretpostavljamo da možemo dobiti korisnika na osnovu userId
    //     Account account = accountService.getAccountById(accountId);
    //     return chatService.getChatRoomsForUser(account);
    // }
    @GetMapping("/getChatRooms/{accountId}")
    public List<ChatRoom> getChatRoomsForUser(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        System.out.println(account.getId() + " " + account.getEmail() + " " + account.getFirstName());
        List<ChatRoom> chatRooms = chatService.getChatRoomsForUser(account);
        System.out.println("Chat rooms fetched: " + chatRooms.size());
        chatRooms.forEach(chat -> System.out.println(chat.getRoomName()));
        return chatRooms;
    }

    // Metoda za dobijanje chat sobe po ID-u
    @GetMapping("/getChatRoom/{chatRoomId}")
    public ChatRoom getChatRoomById(@PathVariable Long chatRoomId) {
        return chatService.getChatRoomById(chatRoomId);
    }
    // Metoda za dobijanje svih korisnika u chat sobi
    @GetMapping("/getUsersInChatRoom/{chatRoomId}")
    public Set<Account> getUsersInChatRoom(@PathVariable Long chatRoomId) {
        return chatService.getUsersInChatRoom(chatRoomId);
    }
}
