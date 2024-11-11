package com.example.onlybuns.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Like;
import com.example.onlybuns.domain.Post;

public interface LikeRepository extends JpaRepository<Like,Long>{
    boolean existsByPostIdAndAccountId(Long postId, Long userId);
    Like findByPostIdAndAccountId(Long postId, Long userId);

    // Metoda za brojanje lajkova za određenu objavu
    long countByPost(Post post);

    // Metoda za proveru da li je korisnik već lajkovao određenu objavu
    boolean existsByPostAndAccount(Post post, Account account);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.account.id = :account " +
           "AND l.creationTime > :sevenDaysAgo")
    long countLikesByAccountInLast7Days(@Param("account") Account account, 
                                        @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
}
