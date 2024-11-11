package com.example.onlybuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Post;

import java.time.LocalDateTime;
import java.util.*;

public interface PostRepository extends JpaRepository<Post,Long>{
    // Metoda za pronalaženje posta po ID-u koji nije logički obrisan
    Optional<Post> findByIdAndIsDeletedFalse(Long id);
    List<Post> findAllByIsDeletedFalse();
    int countByAccountId(Long accountId);
    List<Post> findAllByAccountIdIn(List<Long> accountIds);

    /*@Query("SELECT p FROM Post p WHERE p.account.id IN " +
           "(SELECT f.followed.id FROM Follow f WHERE f.follower.id = :follower) " +
           "AND p.creationTime > :sevenDaysAgo")
    List<Post> findPostsByFollowedAccountsInLast7Days(@Param("follower") Account follower, 
                                                      @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);*/
}
