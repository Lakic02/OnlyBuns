package com.example.onlybuns.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.repository.AccountRepository;
import com.example.onlybuns.repository.FollowRepository;
import com.example.onlybuns.repository.PostRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private NotificationService notificationService;

    
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }
    
    public Page<Account> getAccounts(String firstName, String lastName, String email, String address, Integer minPosts, Integer maxPosts, int page, int size, String sortField, String sortDir) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Account> resultList = new ArrayList<>(accountRepository.findAll());
    
        // Filtriranje na osnovu imena
        if (firstName != null && !firstName.isEmpty()) {
            resultList.retainAll(accountRepository.findByFirstNameContainingIgnoreCase(firstName, PageRequest.of(0, Integer.MAX_VALUE)).getContent());
        }
    
        // Filtriranje na osnovu prezimena
        if (lastName != null && !lastName.isEmpty()) {
            resultList.retainAll(accountRepository.findByLastNameContainingIgnoreCase(lastName, PageRequest.of(0, Integer.MAX_VALUE)).getContent());
        }
    
        // Filtriranje na osnovu email adrese
        if (email != null && !email.isEmpty()) {
            resultList.retainAll(accountRepository.findByEmailContainingIgnoreCase(email, PageRequest.of(0, Integer.MAX_VALUE)).getContent());
        }
    
        // Filtriranje na osnovu adrese
        if (address != null && !address.isEmpty()) {
            resultList.retainAll(accountRepository.findByAddressContainingIgnoreCase(address, PageRequest.of(0, Integer.MAX_VALUE)).getContent());
        }
    
        // Filtriranje po broju postova (minPosts i maxPosts)
        if (minPosts != null || maxPosts != null) {
            resultList = resultList.stream()
                .filter(account -> {
                    int postCount = postRepository.countByAccountId(account.getId());
                    boolean meetsMin = minPosts == null || postCount >= minPosts;
                    boolean meetsMax = maxPosts == null || postCount <= maxPosts;
                    return meetsMin && meetsMax;
                })
                .collect(Collectors.toList());
        }
    
        // Primenjuje se Comparator na osnovu sortField
        Comparator<Account> comparator;
        if (sortField.equalsIgnoreCase("email")) {
            comparator = Comparator.comparing(Account::getEmail, String.CASE_INSENSITIVE_ORDER);
        } else if (sortField.equalsIgnoreCase("followingCount")) {
            comparator = Comparator.comparing(account -> followRepository.countByFollowerId(account.getId()));
        } else {
            // Podrazumevano sortiranje po emailu ako sortField nije prepoznat
            comparator = Comparator.comparing(Account::getEmail, String.CASE_INSENSITIVE_ORDER);
        }
    
        // Ako je pravac sortiranja "desc", obrni Comparator
        if (sortDir.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }
    
        // Sortiraj listu rezultata koristeći izabrani Comparator
        resultList.sort(comparator);
    
        // Ispis svih pronađenih naloga (sortirano i filtrirano)
        // System.out.println("Svi pronađeni nalozi (sortirani):");
        // for (Account account : resultList) {
        //     System.out.println("ID: " + account.getId() +
        //                        ", First Name: " + account.getFirstName() +
        //                        ", Last Name: " + account.getLastName() +
        //                        ", Email: " + account.getEmail());
        // }
    
        // Ako nije pronađeno nijedno podudaranje, vrati praznu listu
        if (resultList.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }
    
        // Kreiranje stranice sa rezultatima
        int start = Math.min((int) pageRequest.getOffset(), resultList.size());
        int end = Math.min((start + pageRequest.getPageSize()), resultList.size());
        
        return new PageImpl<>(resultList.subList(start, end), pageRequest, resultList.size());
    }

    public int countPosts(Long accountId) {
        return postRepository.countByAccountId(accountId);
    }

    public int countFollowing(Long accountId) {
        return followRepository.countByFollowerId(accountId);
    }

    public int countFollowers(Long accountId) {
        return followRepository.countByFollowedId(accountId);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 L * ?", zone = "Europe/Belgrade") // Svaki poslednji dan u mesecu
    public void deleteUnauthenticatedAccounts() {
        LocalDate currentDate = LocalDate.now();
        System.out.println("USLO U BRISANJE NALOGAAAAA");
        
        // Dohvati naloge sa statusom 'unauthenticated'
        List<Account> unauthenticatedAccounts = accountRepository.findByRole(Account.Role.unauthenticated);
        
        for (Account account : unauthenticatedAccounts) {
            // Proverava koliko dana je prošlo od registracije
            //long daysSinceRegistration = ChronoUnit.DAYS.between(account.getCreatedAt(), currentDate);
            
            // Ako je prošlo više od 30 dana, briši nalog
            //if (daysSinceRegistration > 30) {
            //}
            
            accountRepository.delete(account);
            System.out.println("Deleted unauthenticated account with email: " + account.getEmail());
        }
    }

    public void updateLastLogin(Account account) {
        account.setLastLogin(LocalDateTime.now());
        accountRepository.save(account); 
    }

    
    @Scheduled(cron = "0 * * * * ?")
    public void scheduleInactiveUserNotifications() {
        System.out.println("Salje se mejl");
        notificationService.sendInactiveUserNotifications();
    }
}
