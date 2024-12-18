package com.example.onlybuns.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.onlybuns.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/user-activity")
    public Map<String, Double> getUserActivityAnalytics() {
        return analyticsService.getUserActivityPercentages();
    }

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
