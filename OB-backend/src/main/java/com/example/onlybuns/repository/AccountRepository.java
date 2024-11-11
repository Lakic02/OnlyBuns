package com.example.onlybuns.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Account.Role;

import java.util.List;

import org.springframework.data.domain.*;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long>{
    Account findByEmail(String email); //Koristim za prijavu korisnika na sistem
    Account findByUserNameAndPassword(String userName, String password);
    List<Account> findByRole(Role role);

    Page<Account> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
    Page<Account> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
    Page<Account> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<Account> findByAddressContainingIgnoreCase(String address, Pageable pageable);

    Page<Account> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
        String firstName, String lastName, String email, Pageable pageable);

//@Query("SELECT a FROM Account a WHERE (a.postCount >= :minPosts OR :minPosts IS NULL) AND (a.postCount <= :maxPosts OR :maxPosts IS NULL)")
//Page<Account> findByPostCountBetween(@Param("minPosts") Integer minPosts, @Param("maxPosts") Integer maxPosts, Pageable pageable);
}
