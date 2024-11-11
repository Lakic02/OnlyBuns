package com.example.onlybuns.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long>{
  int countByFollowerId(Long followerId);
  int countByFollowedId(Long followedId);

 /*  @Query("SELECT COUNT(f) FROM Follow f WHERE f.followedAccount.id = :account " +
           "AND f.followDate > :sevenDaysAgo")
  long countNewFollowersForAccount(@Param("account") Account account, 
                                      @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);*/
}
