package com.example.onlybuns.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Account.Role;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long>{
    Account findByEmail(String email); //Koristim za prijavu korisnika na sistem
    Account findByUserNameAndPassword(String userName, String password);
    Account findByEmailAndPassword(String email, String password);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :accountId")
    Optional<Account> findByIdWithLock(@Param("accountId") Long accountId);

    List<Account> findByRole(Role role);

    Page<Account> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
    Page<Account> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
    Page<Account> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<Account> findByAddressContainingIgnoreCase(String address, Pageable pageable);

    Page<Account> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
        String firstName, String lastName, String email, Pageable pageable);
    
    @Query("SELECT a FROM Account a WHERE a.lastLogin < :lastActiveDate")
    List<Account> findInactiveUsers(@Param("lastActiveDate") LocalDateTime lastActiveDate);

    // Broj jedinstvenih korisnika koji su napravili objave
    @Query("SELECT COUNT(DISTINCT p.account.id) FROM Post p WHERE p.isDeleted = false")
    long countUsersWithPosts();

    // Broj korisnika koji su napravili samo komentare (bez objava)
    @Query("SELECT COUNT(DISTINCT c.account.id) FROM Comment c WHERE c.account.id NOT IN " +
            "(SELECT DISTINCT p.account.id FROM Post p WHERE p.isDeleted = false)")
    long countUsersWithOnlyComments();

    // Broj korisnika koji nisu napravili nijednu objavu ni komentar
    @Query("SELECT COUNT(a) FROM Account a WHERE a.id NOT IN " +
            "(SELECT DISTINCT p.account.id FROM Post p WHERE p.isDeleted = false) AND a.id NOT IN " +
            "(SELECT DISTINCT c.account.id FROM Comment c)")
    long countInactiveUsers();
    

//@Query("SELECT a FROM Account a WHERE (a.postCount >= :minPosts OR :minPosts IS NULL) AND (a.postCount <= :maxPosts OR :maxPosts IS NULL)")
//Page<Account> findByPostCountBetween(@Param("minPosts") Integer minPosts, @Param("maxPosts") Integer maxPosts, Pageable pageable);
}
