package com.example.onlybuns.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.onlybuns.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long>{
    Account findByEmail(String email); //Koristim za prijavu korisnika na sistem

}
