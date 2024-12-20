package com.example.onlybuns.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.onlybuns.repository.AccountRepository;
import com.example.onlybuns.repository.CommentRepository;
import com.example.onlybuns.repository.PostRepository;

@Service
public class AnalyticsService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AccountRepository accountRepository;

    public Map<String, Double> getUserActivityPercentages() {
        long totalUsers = accountRepository.count();
        long usersWithPosts = accountRepository.countUsersWithPosts();
        long usersWithOnlyComments = accountRepository.countUsersWithOnlyComments();
        long inactiveUsers = accountRepository.countInactiveUsers();

        // Računanje procenata
        Map<String, Double> response = new HashMap<>();
        response.put("usersWithPostsPercentage", (usersWithPosts * 100.0) / totalUsers);
        response.put("usersWithOnlyCommentsPercentage", (usersWithOnlyComments * 100.0) / totalUsers);
        response.put("inactiveUsersPercentage", (inactiveUsers * 100.0) / totalUsers);

        return response;
    }


    public long getWeeklyPostCount() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(1, ChronoUnit.WEEKS);
        return postRepository.countPostsByDateRange(startDate, endDate);
    }

    public long getMonthlyPostCount() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(1, ChronoUnit.MONTHS);
        return postRepository.countPostsByDateRange(startDate, endDate);
    }

    public long getYearlyPostCount() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(1, ChronoUnit.YEARS);
        return postRepository.countPostsByDateRange(startDate, endDate);
    }

    public long getWeeklyCommentCount() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(1, ChronoUnit.WEEKS);
        return commentRepository.countCommentsByDateRange(startDate, endDate);
    }

    public long getMonthlyCommentCount() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(1, ChronoUnit.MONTHS);
        return commentRepository.countCommentsByDateRange(startDate, endDate);
    }

    public long getYearlyCommentCount() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(1, ChronoUnit.YEARS);
        return commentRepository.countCommentsByDateRange(startDate, endDate);
    }
}
