package com.example.onlybuns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Post;
import com.example.onlybuns.repository.AccountRepository;
import com.example.onlybuns.repository.FollowRepository;
import com.example.onlybuns.repository.LikeRepository;
import com.example.onlybuns.repository.PostRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private FollowRepository followRepository;

    public void sendInactiveUserNotifications() {
        List<Account> inactiveUsers = accountRepository.findInactiveUsers(LocalDateTime.now().minusDays(7));
        
        for (Account user : inactiveUsers) {
            sendEmail(user);
        }
    }

    private void sendEmail(Account account) {
        long likesCount = getLikesCountForLast7Days(account);
        //long newFollowersCount = getNewFollowersCountForLast7Days(account);
        //long postsCount = getNewPostsFromFollowedAccounts(account);
    
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getEmail());
        message.setSubject("Recent Activities on Your Account");
    
        String emailText = String.format(
            "Dear %s,\n\n" +
            "In the last 7 days, the following activities have been recorded on your account:\n\n" +
            "- %d new likes on your posts\n" +
            "- new followers\n" +
            "-  new posts\n\n" +
            "Thank you for using our app! Enjoy your continued experience.",
            account.getFirstName(), likesCount/*, newFollowersCount, postsCount */ );
    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        message.setText(emailText);
        System.out.println("aaaaagyftydr4e4ss6t76ygaaaaaaaaaaaaaaaaaaaaaaaaa");
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Transactional
    public long getLikesCountForLast7Days(Account account) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return likeRepository.countLikesByAccountInLast7Days(account, sevenDaysAgo);
    }

    /*@Transactional
    public long getNewPostsFromFollowedAccounts(Account account) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Post> newPosts = postRepository.findPostsByFollowedAccountsInLast7Days(account, sevenDaysAgo);
        return newPosts.size();
    }

    @Transactional
    public long getNewFollowersCountForLast7Days(Account account) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return followRepository.countNewFollowersForAccount(account, sevenDaysAgo);
    }*/
}