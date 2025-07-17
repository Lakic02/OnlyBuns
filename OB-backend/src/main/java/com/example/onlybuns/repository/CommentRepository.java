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

    // @Query(value = "SELECT EXTRACT(YEAR FROM creation_time) AS year, COUNT(*) AS count FROM comments WHERE is_deleted = false GROUP BY year ORDER BY year", nativeQuery = true)
    // List<Object[]> countCommentsByYear();

    // @Query(value = "SELECT EXTRACT(YEAR FROM creation_time) AS year, EXTRACT(MONTH FROM creation_time) AS month, COUNT(*) AS count FROM comments WHERE is_deleted = false GROUP BY year, month ORDER BY year, month", nativeQuery = true)
    // List<Object[]> countCommentsByMonth();

    // @Query(value = "SELECT EXTRACT(YEAR FROM creation_time) AS year, EXTRACT(MONTH FROM creation_time) AS month, EXTRACT(WEEK FROM creation_time) AS week, COUNT(*) AS count FROM comments WHERE is_deleted = false GROUP BY year, month, week ORDER BY year, month, week", nativeQuery = true)
    // List<Object[]> countCommentsByWeek();

    @Query("SELECT COUNT(c) FROM Comment c WHERE EXTRACT(YEAR FROM c.creationTime) = :year")
    long countCommentsByYear(@Param("year") int year);

    // Metode za filtriranje po godini i mesecima
    @Query(value = "SELECT COUNT(*) FROM comments WHERE EXTRACT(YEAR FROM creation_time) = :year AND EXTRACT(MONTH FROM creation_time) = :month", nativeQuery = true)
    long countCommentsByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // Metode za dobavljanje komentara po nedeljama za određenu godinu i mesec
    @Query(value = "SELECT EXTRACT(WEEK FROM creation_time) AS week, COUNT(*) AS count FROM comments WHERE EXTRACT(YEAR FROM creation_time) = :year AND EXTRACT(MONTH FROM creation_time) = :month AND is_deleted = false GROUP BY week ORDER BY week", nativeQuery = true)
    List<Object[]> countCommentsByWeekForYearAndMonth(@Param("year") int year, @Param("month") int month);
}
