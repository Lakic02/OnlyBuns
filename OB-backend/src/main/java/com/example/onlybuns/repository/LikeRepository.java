package com.example.onlybuns.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Like;
import com.example.onlybuns.domain.Post;

import jakarta.persistence.LockModeType;

public interface LikeRepository extends JpaRepository<Like,Long>{
    boolean existsByPostIdAndAccountId(Long postId, Long userId);
    Like findByPostIdAndAccountId(Long postId, Long userId);
    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    // @Query("SELECT l FROM Like l WHERE l.post.id = :postId AND l.account.id = :userId")
    // Optional<Like> findByPostIdAndAccountIdWithLock(@Param("postId") Long postId, @Param("userId") Long userId);

    // Metoda za brojanje lajkova za određenu objavu
    long countByPost(Post post);

    // Metoda za proveru da li je korisnik već lajkovao određenu objavu
    boolean existsByPostAndAccount(Post post, Account account);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.account.id = :accountId AND l.creationTime > :sevenDaysAgo")
    long countLikesByAccountInLast7Days(@Param("accountId") Long accountId, 
                                    @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

    @Query("SELECT l.account.id, COUNT(l.id) AS likeCount " +
       "FROM Like l " +
       "WHERE l.creationTime >= :sevenDaysAgo " +
       "GROUP BY l.account.id " +
       "ORDER BY likeCount DESC")
    List<Object[]> findTopAccountsLast7Days(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

}
