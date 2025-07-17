package com.example.onlybuns.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlybuns.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AnalyticsController {
    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/user-activity")
    public Map<String, Double> getUserActivityAnalytics() {
        return analyticsService.getUserActivityPercentages();
    }

    @GetMapping("/posts-by-year/{year}")
    public long getPostsByYear(@PathVariable int year) {
        return analyticsService.getPostCountByYear(year);
    }

    @GetMapping("/comments-by-year/{year}")
    public long getCommentsByYear(@PathVariable int year) {
        return analyticsService.getCommentCountByYear(year);
    }

    @GetMapping("/posts-by-month/{year}/{month}")
    public long getPostsByMonth(@PathVariable int year, @PathVariable int month) {
        return analyticsService.getPostCountByMonth(year, month);
    }

    @GetMapping("/comments-by-month/{year}/{month}")
    public long getCommentsByMonth(@PathVariable int year, @PathVariable int month) {
        return analyticsService.getCommentCountByMonth(year, month);
    }

    @GetMapping("/weeks-in-month/{year}/{month}")
    public List<Map<String, Object>> getWeeksInMonth(@PathVariable int year, @PathVariable int month) {
        return analyticsService.getWeeklyDataForMonth(year, month);
    }

    // @GetMapping("/posts-per-year")
    // public List<Map<String, Object>> getPostsPerYear() {
    //     return analyticsService.getPostsPerYear();
    // }

    // @GetMapping("/posts-per-month")
    // public List<Map<String, Object>> getPostsPerMonth() {
    //     return analyticsService.getPostsPerMonth();
    // }

    // @GetMapping("/posts-per-week")
    // public List<Map<String, Object>> getPostsPerWeek() {
    //     return analyticsService.getPostsPerWeek();
    // }

    @GetMapping("/posts")
    public Map<String, Long> getPostAnalytics() {
        Map<String, Long> response = new HashMap<>();
        response.put("weeklyPosts", analyticsService.getWeeklyPostCount());
        response.put("monthlyPosts", analyticsService.getMonthlyPostCount());
        response.put("yearlyPosts", analyticsService.getYearlyPostCount());
        return response;
    }

    @GetMapping("/comments")
    public Map<String, Long> getCommentAnalytics() {
        Map<String, Long> response = new HashMap<>();
        response.put("weeklyComments", analyticsService.getWeeklyCommentCount());
        response.put("monthlyComments", analyticsService.getMonthlyCommentCount());
        response.put("yearlyComments", analyticsService.getYearlyCommentCount());
        return response;
    }
}
