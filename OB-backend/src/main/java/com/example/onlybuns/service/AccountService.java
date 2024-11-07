package com.example.onlybuns.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.repository.AccountRepository;
import com.example.onlybuns.repository.FollowRepository;
import com.example.onlybuns.repository.PostRepository;

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

    public Page<Account> getAccounts(String firstName, String lastName, String email, String address, Integer minPosts, Integer maxPosts, int page, int size, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Prvo, pretrazi sve filtere odvojeno
        Page<Account> firstNamePage = firstName != null && !firstName.isEmpty() ?
            accountRepository.findByFirstNameContainingIgnoreCase(firstName, pageRequest) : null;

        Page<Account> lastNamePage = lastName != null && !lastName.isEmpty() ?
            accountRepository.findByLastNameContainingIgnoreCase(lastName, pageRequest) : null;

        Page<Account> emailPage = email != null && !email.isEmpty() ?
            accountRepository.findByEmailContainingIgnoreCase(email, pageRequest) : null;
            
        Page<Account> addressPage = address != null && !address.isEmpty() ?
            accountRepository.findByAddressContainingIgnoreCase(address, pageRequest) : null;

        // Ako su svi filteri prazni, vrati sve korisnike
        if (firstName == null && lastName == null && email == null && address == null && 
            (minPosts == null || minPosts == 0) && (maxPosts == null || maxPosts == 0)) {
                System.out.println("UZMI SVEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            return accountRepository.findAll(pageRequest);
        }

        // Ako je samo jedan filter postavljen, vraćaju se odgovarajući rezultati
        List<Account> resultList = new ArrayList<>(accountRepository.findAll());

        if (firstNamePage != null) {
            resultList.retainAll(firstNamePage.getContent());
        }
        if (lastNamePage != null) {
            resultList.retainAll(lastNamePage.getContent());  // Presek sa lastName pretragom
        }
        if (emailPage != null) {
            resultList.retainAll(emailPage.getContent());  // Presek sa email pretragom
        }
        if (addressPage != null) {
            resultList.retainAll(addressPage.getContent());  // Presek sa adress pretragom
        }

        // Filtriraj korisnike po broju objava na osnovu minPosts i maxPosts
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


        // Ako nije pronađeno nijedno podudaranje, vrati praznu listu
        if (resultList.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }

        // Ako je pronađeno odgovarajuće podudaranje, vrati ih kao stranicu
        return new PageImpl<>(resultList, pageRequest, resultList.size());
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




  /*public Page<Account> getAccounts(String firstName, String lastName, String email, Integer minPosts, Integer maxPosts, int page, int size, String sortField, String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
    PageRequest pageRequest = PageRequest.of(page, size, sort);
    System.out.println("PAGE REQ: "+pageRequest);
    // if (minPosts != null || maxPosts != null) {
    //     return accountRepository.findByPostCountBetween(minPosts, maxPosts, pageRequest);
    // } else {
    Page<Account> accounts = accountRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndEmailContainingIgnoreCase(firstName, lastName, email, pageRequest);
    System.out.println(":::ACCOUNTS:::");
    System.out.println(accounts);
    return accounts;
    // }
  }*/

//   public Page<Account> getAccounts(String firstName, String lastName, String email, Integer minPosts, Integer maxPosts, int page, int size, String sortField, String sortDir) {
//     Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
//     PageRequest pageRequest = PageRequest.of(page, size, sort);

//     // Provera da li su filteri prazni ili null
//     boolean isFilterEmpty = (firstName == null || firstName.isEmpty()) &&
//                             (lastName == null || lastName.isEmpty()) &&
//                             (email == null || email.isEmpty()) &&
//                             minPosts == null && maxPosts == null;

//     if (isFilterEmpty) {
//         // Ako su svi filteri prazni, vrati sve korisnike
//         return accountRepository.findAll(pageRequest);
//     } else {
//         // Dinamički konstruisanje uslova za filtere
//         if (firstName != null && !firstName.isEmpty()) {
//             return accountRepository.findByFirstNameContainingIgnoreCase(firstName, pageRequest);
//         }
//         if (lastName != null && !lastName.isEmpty()) {
//             return accountRepository.findByLastNameContainingIgnoreCase(lastName, pageRequest);
//         }
//         if (email != null && !email.isEmpty()) {
//             return accountRepository.findByEmailContainingIgnoreCase(email, pageRequest);
//         }
//         // Ako su postavke za postove zadate
//         // if (minPosts != null || maxPosts != null) {
//         //     return accountRepository.findByPostCountBetween(minPosts, maxPosts, pageRequest);
//         // }
        
//         // Ako ima više filtera, mogu se dodati dodatni uslovi kombinovani (logika se može proširiti)
//         return accountRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndEmailContainingIgnoreCase(firstName, lastName, email, pageRequest);
//     }
// }

}
