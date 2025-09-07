package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.onlybuns.OnlyBunsApplication;
import com.example.onlybuns.domain.Account;
import com.example.onlybuns.service.AccountService;
import com.example.onlybuns.service.AuthenticationService;

@SpringBootTest(classes = OnlyBunsApplication.class)
public class UserRegistrationConcurrencyTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    void testConcurrentRegistration() throws InterruptedException {

        String username = "testUser";
        
        Account newAccount = new Account();
        newAccount.setUserName(username);
        newAccount.setEmail("test@mail.com");
        newAccount.setPassword("password123");
        newAccount.setFirstName("Test");
        newAccount.setLastName("User");
        newAccount.setAddress("123 Test St");
        newAccount.setRole(Account.Role.unauthenticated);

        Runnable task = () -> {
            try {
                authenticationService.registerAccount(newAccount);
                System.out.println(Thread.currentThread().getName() + ": Registration SUCCESS");
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + ": Registration FAILED -> " + e.getMessage());
            }
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
