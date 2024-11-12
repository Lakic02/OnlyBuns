package com.example.onlybuns.repository;

import java.util.List;
import java.util.Optional;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long>{
  int countByFollowerId(Long followerId);
  int countByFollowedId(Long followedId);    
  Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

  @Query("SELECT f.followed.id FROM Follow f WHERE f.follower.id = :userId")
  List<Long> findFollowedUserIdsByUserId(@Param("userId") Long userId);
  
  @Query("SELECT f.follower FROM Follow f WHERE f.followed.id = :followedId")
  List<Account> findByFollowedId(Long followedId);

  
  @Query("SELECT COUNT(f) > 0 FROM Follow f WHERE f.follower.id = ?1 AND f.followed.id = ?2")
  boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);


  @Query("SELECT COUNT(f) FROM Follow f WHERE f.followed.id = :accountId AND f.followDate > :sevenDaysAgo")
  long countNewFollowersForAccount(@Param("accountId") Long accountId, 
                                 @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

}
