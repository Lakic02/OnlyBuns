package com.example.onlybuns.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public long getPostCountByYear(int year) {
        return postRepository.countPostsByYear(year);
    }

    public long getCommentCountByYear(int year) {
        return commentRepository.countCommentsByYear(year);
    }
    
    public long getPostCountByMonth(int year, int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
        return postRepository.countPostsByDateRange(startDate, endDate);
    }

    public long getCommentCountByMonth(int year, int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
        return commentRepository.countCommentsByDateRange(startDate, endDate);
    }

    public List<Map<String, Object>> getWeeklyDataForMonth(int year, int month) {
        List<Map<String, Object>> weeklyData = new ArrayList<>();
        
        LocalDateTime monthStart = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);
        
        // Počni od početka nedelje koja sadrži prvi dan meseca
        LocalDateTime weekStart = monthStart.with(java.time.DayOfWeek.MONDAY);
        
        int weekNumber = 1;
        while (weekStart.isBefore(monthEnd)) {
            LocalDateTime weekEnd = weekStart.plusWeeks(1).minusSeconds(1);
            
            // Ako je kraj nedelje nakon kraja meseca, ograniči na kraj meseca
            if (weekEnd.isAfter(monthEnd)) {
                weekEnd = monthEnd;
            }
            
            long posts = postRepository.countPostsByDateRange(weekStart, weekEnd);
            long comments = commentRepository.countCommentsByDateRange(weekStart, weekEnd);
            
            Map<String, Object> weekData = new HashMap<>();
            weekData.put("weekNumber", weekNumber);
            weekData.put("startDate", weekStart.toLocalDate());
            weekData.put("endDate", weekEnd.toLocalDate());
            weekData.put("posts", posts);
            weekData.put("comments", comments);
            
            weeklyData.add(weekData);
            
            weekStart = weekStart.plusWeeks(1);
            weekNumber++;
        }
        
        return weeklyData;
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

    // public List<Map<String, Object>> getPostsPerYear() {
    //     List<Object[]> results = postRepository.countPostsByYear();
    //     List<Map<String, Object>> list = new ArrayList<>();
    //     for (Object[] row : results) {
    //         Map<String, Object> map = new HashMap<>();
    //         map.put("year", row[0]);
    //         map.put("count", row[1]);
    //         list.add(map);
    //     }
    //     return list;
    // }

    // public List<Map<String, Object>> getPostsPerMonth() {
    //     List<Object[]> results = postRepository.countPostsByMonth();
    //     List<Map<String, Object>> list = new ArrayList<>();
    //     for (Object[] row : results) {
    //         Map<String, Object> map = new HashMap<>();
    //         map.put("year", row[0]);
    //         map.put("month", row[1]);
    //         map.put("count", row[2]);
    //         list.add(map);
    //     }
    //     return list;
    // }

    // public List<Map<String, Object>> getPostsPerWeek() {
    //     List<Object[]> results = postRepository.countPostsByWeek();
    //     List<Map<String, Object>> list = new ArrayList<>();
    //     for (Object[] row : results) {
    //         Map<String, Object> map = new HashMap<>();
    //         map.put("year", row[0]);
    //         map.put("week", row[1]);
    //         map.put("count", row[2]);
    //         list.add(map);
    //     }
    //     return list;
    // }

    // public List<Map<String, Object>> getCommentsPerYear() {
    //     List<Object[]> results = commentRepository.countCommentsByYear();
    //     List<Map<String, Object>> list = new ArrayList<>();
    //     for (Object[] row : results) {
    //         Map<String, Object> map = new HashMap<>();
    //         map.put("year", row[0]);
    //         map.put("count", row[1]);
    //         list.add(map);
    //     }
    //     return list;
    // }

    // public List<Map<String, Object>> getCommentsPerMonth() {
    //     List<Object[]> results = commentRepository.countCommentsByMonth();
    //     List<Map<String, Object>> list = new ArrayList<>();
    //     for (Object[] row : results) {
    //         Map<String, Object> map = new HashMap<>();
    //         map.put("year", row[0]);
    //         map.put("month", row[1]);
    //         map.put("count", row[2]);
    //         list.add(map);
    //     }
    //     return list;
    // }

    // public List<Map<String, Object>> getCommentsPerWeek() {
    //     List<Object[]> results = commentRepository.countCommentsByWeek();
    //     List<Map<String, Object>> list = new ArrayList<>();
    //     for (Object[] row : results) {
    //         Map<String, Object> map = new HashMap<>();
    //         map.put("year", row[0]);
    //         map.put("week", row[1]);
    //         map.put("count", row[2]);
    //         list.add(map);
    //     }
    //     return list;
    // }
}
