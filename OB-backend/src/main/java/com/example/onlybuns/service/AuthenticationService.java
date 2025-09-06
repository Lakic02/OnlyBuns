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
    @Autowired
    private BloomFilterService bloomFilterService;

    //public Account updateAccount(Account acc){ return accountRepository.update(acc);}


    public Account registerAccount(Account acc) {
        // Proverimo Bloom filter
        if (bloomFilterService.mightExist(acc.getUserName())) {
            // Postoji mala verovatnoća da je false positive
            if (accountRepository.findByUserNameAndPassword(acc.getUserName(), acc.getPassword()) != null) {
                throw new RuntimeException("Username is already taken");
            }
        }

        Account savedAcc = accountRepository.save(acc);

        // Dodajemo korisničko ime u Bloom filter
        bloomFilterService.addUserName(savedAcc.getUserName());

        return savedAcc;
    }
    public Account findAccountByUserNameAndPassword(String userName, String password) {
        return accountRepository.findByUserNameAndPassword(userName, password);
    }
    public Account findAccountByEmailAndPassword(String email,String password){
        return  accountRepository.findByEmailAndPassword(email,password);
    }

}
