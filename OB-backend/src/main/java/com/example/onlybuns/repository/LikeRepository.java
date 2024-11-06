package com.example.onlybuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onlybuns.domain.Like;

public interface LikeRepository extends JpaRepository<Like,Long>{
    boolean existsByPostIdAndAccountId(Long postId, Long userId);
}
