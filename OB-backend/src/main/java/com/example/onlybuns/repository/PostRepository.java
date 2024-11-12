package com.example.onlybuns.repository;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Follow;
import com.example.onlybuns.domain.Post;

import java.time.LocalDateTime;
import java.util.*;

public interface PostRepository extends JpaRepository<Post,Long>{
    // Metoda za pronalaženje posta po ID-u koji nije logički obrisan
    Optional<Post> findByIdAndIsDeletedFalse(Long id);
    List<Post> findAllByIsDeletedFalse();
    int countByAccountId(Long accountId);
    List<Post> findAllByAccountIdIn(List<Long> accountIds);

    @Query("SELECT p FROM Post p WHERE p.account IN (SELECT f.followed FROM Follow f WHERE f.follower.id = :followerId) AND p.creationTime > :sevenDaysAgo")
    List<Post> findPostsByFollowedAccountsInLast7Days(@Param("followerId") Long followerId, 
                                                  @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

}
