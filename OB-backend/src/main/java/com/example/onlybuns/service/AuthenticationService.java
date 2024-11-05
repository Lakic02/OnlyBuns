package com.example.onlybuns.service;
import com.example.onlybuns.domain.Account;
import com.example.onlybuns.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@SuppressWarnings("unused")


@Service
public class AuthenticationService {
    @Autowired
    private AccountRepository accountRepository;

    public void registerAccount(Account acc){
        accountRepository.save(acc);
    }



}
