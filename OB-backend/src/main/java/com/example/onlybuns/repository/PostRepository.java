package com.example.onlybuns.repository;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Follow;
import com.example.onlybuns.domain.Post;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.annotations.QueryHints;
import jakarta.persistence.QueryHint;

public interface PostRepository extends JpaRepository<Post,Long>{
    // Metoda za pronalaženje posta po ID-u koji nije logički obrisan
    Optional<Post> findByIdAndIsDeletedFalse(Long id);
    List<Post> findAllByIsDeletedFalse();
    int countByAccountId(Long accountId);
    List<Post> findAllByAccountIdIn(List<Long> accountIds);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :postId")
    Optional<Post> findByIdWithLock(@Param("postId") Long postId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.creationTime BETWEEN :startDate AND :endDate AND p.isDeleted = false")
    long countPostsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // @Query(value = "SELECT EXTRACT(YEAR FROM creation_time) AS year, COUNT(*) AS count FROM posts WHERE is_deleted = false GROUP BY year ORDER BY year", nativeQuery = true)
    // List<Object[]> countPostsByYear();

    // @Query(value = "SELECT EXTRACT(YEAR FROM creation_time) AS year, EXTRACT(MONTH FROM creation_time) AS month, COUNT(*) AS count FROM posts WHERE is_deleted = false GROUP BY year, month ORDER BY year, month", nativeQuery = true)
    // List<Object[]> countPostsByMonth();

    // @Query(value = "SELECT EXTRACT(YEAR FROM creation_time) AS year, EXTRACT(MONTH FROM creation_time) AS month, EXTRACT(WEEK FROM creation_time) AS week, COUNT(*) AS count FROM posts WHERE is_deleted = false GROUP BY year, month, week ORDER BY year, month, week", nativeQuery = true)
    // List<Object[]> countPostsByWeek();

    @Query("SELECT p FROM Post p WHERE p.account IN (SELECT f.followed FROM Follow f WHERE f.follower.id = :followerId) AND p.creationTime > :sevenDaysAgo")
    List<Post> findPostsByFollowedAccountsInLast7Days(@Param("followerId") Long followerId, 
                                                  @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
                                                  
    // metode za filtriranje po godini
    @Query(value = "SELECT COUNT(*) FROM posts WHERE EXTRACT(YEAR FROM creation_time) = :year AND is_deleted = false", nativeQuery = true)
    long countPostsByYear(@Param("year") int year);

    // metode za filtriranje po godini i mesecima
    @Query(value = "SELECT COUNT(*) FROM posts WHERE EXTRACT(YEAR FROM creation_time) = :year AND EXTRACT(MONTH FROM creation_time) = :month AND is_deleted = false", nativeQuery = true)
    long countPostsByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // metode za dobavljanje postova po nedeljama za određenu godinu i mesec
    @Query(value = "SELECT EXTRACT(WEEK FROM creation_time) AS week,COUNT(*) AS count FROM posts WHERE EXTRACT(YEAR FROM creation_time) = :year AND EXTRACT(MONTH FROM creation_time) = :month AND is_deleted = false GROUP BY week ORDER BY week", nativeQuery = true)
    List<Object[]> countPostsByWeekForYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query(value = "SELECT l.post_id, COUNT(*) AS like_count " +
                   "FROM likes l " +
                   "JOIN posts p ON l.post_id = p.id " +
                   "WHERE p.is_deleted = false " +
                   "AND l.creation_time >= NOW() - INTERVAL '7 days' " +
                   "GROUP BY l.post_id " +
                   "ORDER BY like_count DESC", nativeQuery = true)
    @org.springframework.data.jpa.repository.QueryHints(
    value = {
        @QueryHint(name = QueryHints.CACHEABLE, value = "true")
    }
    )
    List<Object[]> findMostLikedPostsLast7Days();

    
    @Query(value = "SELECT l.post_id, COUNT(*) AS like_count " +
               "FROM likes l " +
               "JOIN posts p ON l.post_id = p.id " +
               "WHERE p.is_deleted = false " +
               "GROUP BY l.post_id " +
               "ORDER BY like_count DESC " +
               "LIMIT 10", nativeQuery = true)
    @org.springframework.data.jpa.repository.QueryHints(
    value = {
        @QueryHint(name = QueryHints.CACHEABLE, value = "true")
    }
    )
    List<Object[]> findTop10MostLikedPosts();

    @Query(value = "SELECT COUNT(*) " +
                   "FROM posts p " +
                   "WHERE p.is_deleted = false " +
                   "AND p.creation_time >= NOW() - INTERVAL '7 days'", nativeQuery = true)
    Long countPostsCreatedLast7Days();
}
