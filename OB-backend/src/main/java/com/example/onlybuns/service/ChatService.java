package com.example.onlybuns.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;


import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.ChatMessage;
import com.example.onlybuns.domain.ChatRoom;
import com.example.onlybuns.domain.ChatRoomMembership;
import com.example.onlybuns.repository.AccountRepository;
import com.example.onlybuns.repository.ChatMessageRepository;
import com.example.onlybuns.repository.ChatRoomMembershipRepository;
import com.example.onlybuns.repository.ChatRoomRepository;
import org.springframework.data.domain.Pageable;


@Service
public class ChatService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private ChatRoomMembershipRepository chatRoomMembershipRepository;

    // Čuvanje nove poruke u bazi
    public ChatMessage saveMessage(ChatMessage message) throws AccountNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findById(message.getSender().getId());
        if (optionalAccount.isPresent()) {
            //System.out.println("SACUVALO ACCOUNT ZA PORUKU");
            message.setSender(optionalAccount.get());  // Uzimate Account iz Optional-a
        } else {
            // Obrada u slučaju da Account nije pronađen, na primer bacanje izuzetka
            throw new AccountNotFoundException("Account not found for ID: " + message.getSender().getId());
        }

        return chatMessageRepository.save(message);
    }

    // Prikaz svih poruka za određeni grupni čet
    public List<ChatMessage> getMessagesByChatRoom(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);
    }

    // Prikaz poslednjih N poruka za grupni čet
    public List<ChatMessage> getLastNMessagesByChatRoom(Long chatRoomId, int count) {
        return chatMessageRepository.findByChatRoomIdOrderByTimestampDesc(chatRoomId, PageRequest.of(0, count));
    }

    // Kreira novi grupni čet sa zadatim adminom
    public ChatRoom createChatRoom(String roomName, Long adminId) {
        Account admin = accountRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid admin ID"));

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomName(roomName);
        chatRoom.setAdmin(admin);

        return chatRoomRepository.save(chatRoom);
    }
    public void removeChatRoomMembership(Long chatRoomId, Long accountId) {
        // Pronađi članstvo na osnovu ID-a sobe i korisnika
        ChatRoomMembership membership = chatRoomMembershipRepository
                .findByChatRoomIdAndAccountId(chatRoomId, accountId);

        if (membership == null) {
            throw new RuntimeException("Membership not found for the given chat room and user.");
        }
    
        // Obriši članstvo
        chatRoomMembershipRepository.delete(membership);
    }
    

    @Transactional
    public void addUserToChatRoom(Long chatRoomId, Account user) {
        // ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("Chat room not found"));
        // chatRoom.getMembers().add(user);
        // chatRoomRepository.save(chatRoom);
        // if (chatRoomRepository.existsById(chatRoomId)) {
        //     chatRoomRepository.addMemberToChatRoom(chatRoomId, user.getId());/////////////////////////////////////////////////////////
        // } else {
        //     throw new RuntimeException("Chat room not found");
        // }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));

        // Proverite da li je korisnik već član
        boolean isMember = chatRoom.getMemberships().stream()
            .anyMatch(membership -> membership.getAccount().getId() == user.getId());
        if (isMember) {
            throw new RuntimeException("User is already a member of this chat room.");
        }
        
        // Kreirajte novu instancu ChatRoomMembership
        ChatRoomMembership membership = new ChatRoomMembership();
        membership.setChatRoom(chatRoom);
        membership.setAccount(user);
        membership.setJoinedAt(LocalDateTime.now());
        System.out.println(membership.getChatRoom());
        System.out.println(membership.getAccount().getEmail() + membership.getAccount().getFirstName());
        System.out.println(membership.toString());


        // Dodajte članstvo u set i sačuvajte
        //System.out.println("BROJ PRE: " + chatRoom.getMemberships().size());
        //chatRoom.getMemberships().add(membership);
        //System.out.println("BROJ POSLE: " + chatRoom.getMemberships().size());
        //System.out.println("--------------------------------------------------");
        //System.out.println("BROJ PRE: " + chatRoomRepository.count());
        //chatRoomRepository.save(chatRoom);
        chatRoomMembershipRepository.save(membership);
        //System.out.println("BROJ POSLE: " + chatRoomRepository.count());

        // // Proveri da li je korisnik već član sobe
        // if (chatRoom.getMembers().contains(user)) {
        //     throw new RuntimeException("User is already a member of this chat room.");
        // }

        // // Dodaj korisnika u članove i sačuvaj izmene
        // chatRoom.getMembers().add(user);
        // chatRoomRepository.save(chatRoom);
    }
    

    public List<ChatRoom> getChatRoomsForUser(Account account) {
        //return chatRoomRepository.findByAdminOrMembersContaining(account, account);
        //return chatRoomRepository.findByAdminOrMember(account);findChatRoomsForUser
        return chatRoomRepository.findChatRoomsForUser(account.getId());
    }
    // Metoda za pronalaženje chat sobe po ID-u
    public ChatRoom getChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + chatRoomId));
    }
    
    // Metoda za dobijanje svih korisnika u chat sobi
    public Set<Account> getUsersInChatRoom(Long chatRoomId) {
        // Proverava da li chat room postoji
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new RuntimeException("Chat room not found with id: " + chatRoomId);
        }

        // Dohvata članstva za zadatu sobu
        List<ChatRoomMembership> memberships = chatRoomMembershipRepository.findByChatRoomId(chatRoomId);

        // Mapira članstva u korisnike (Account) i vraća kao Set
        return memberships.stream()
                        .map(ChatRoomMembership::getAccount)
                        .collect(Collectors.toSet());
        // ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        //         .orElseThrow(() -> new RuntimeException("Chat room not found with id: " + chatRoomId));
        // return chatRoom.getMembers();  // Vraća set svih članova chat sobe
    }
    // public List<ChatMessage> getMessagesBeforeJoining(Long chatRoomId, LocalDateTime userJoinDate) {
    //     return chatMessageRepository.findMessagesBeforeDate(chatRoomId, userJoinDate);
    // }
    // public List<ChatMessage> getMessagesAfterDate(Long chatRoomId, LocalDateTime date, int limit) {
    //     // Kreirajte Pageable objekat sa limitom
    //     Pageable pageable = PageRequest.of(0, limit); // 0 je broj stranice, limit je broj poruka po stranici
    //     return chatMessageRepository.findMessagesAfterDate(chatRoomId, date, pageable);
    // }

    // Dohvati poruke koje su poslate pre nego što je korisnik postao član
    public List<ChatMessage> getMessagesBeforeJoining(Long chatRoomId, Long accountId) {
        // Dobavi datum kada je korisnik postao član
        LocalDateTime userJoinDate = chatRoomMembershipRepository
            .findByChatRoomIdAndAccountId(chatRoomId, accountId)
            .getJoinedAt();

        // Kreiraj Pageable za limit od 10 poruka
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("timestamp"))); // Prvih 10 poruka pre datuma

        // Dohvati poslednjih 10 poruka koje su poslate pre nego što je korisnik postao član
        List<ChatMessage> chatMessages = chatMessageRepository.findMessagesBeforeDate(chatRoomId, userJoinDate, pageable);
        return chatMessages.reversed();
    }

    // Dohvati poslednjih 10 poruka nakon što je korisnik postao član
    public List<ChatMessage> getLastMessagesAfterJoining(Long chatRoomId, Long accountId) {
        // Dobavi datum kada je korisnik postao član
        LocalDateTime userJoinDate = chatRoomMembershipRepository
            .findByChatRoomIdAndAccountId(chatRoomId, accountId)
            .getJoinedAt();

        // Dohvati poslednjih 10 poruka nakon što je korisnik postao član
        //Pageable pageable = PageRequest.of(0, limit); // 0 je broj stranice, limit je broj poruka po stranici

        //TODO PRETHODNIH 10
        return chatMessageRepository.findMessagesAfterDate(chatRoomId, userJoinDate);
    }
    public boolean isUserAdmin(Long chatRoomId, Long userId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new RuntimeException("Chat room not found"));

        // Check if the userId matches the admin's ID
        return chatRoom.getAdmin().getId() == userId;
    }
}
