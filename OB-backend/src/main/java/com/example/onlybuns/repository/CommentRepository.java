package com.example.onlybuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlybuns.domain.Comment;
import com.example.onlybuns.domain.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    // Metoda za preuzimanje svih komentara za određenu objavu
    List<Comment> findByPost(Post post);

    @Query("SELECT COUNT(DISTINCT c.account.id) FROM Comment c")
    long countUsersWithComments();

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.account.id = :accId AND c.creationTime > :oneHourAgo")
    long countByAccountIdAndCreationTimeAfter(@Param("accId") Long accId, @Param("oneHourAgo") LocalDateTime oneHourAgo);    
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.creationTime BETWEEN :startDate AND :endDate")
    long countCommentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
