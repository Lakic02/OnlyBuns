package com.example.onlybuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onlybuns.domain.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long>{
  int countByFollowerId(Long followerId);
  int countByFollowedId(Long followedId);
}
