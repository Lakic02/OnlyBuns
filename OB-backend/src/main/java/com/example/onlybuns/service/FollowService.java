package com.example.onlybuns.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.repository.AccountRepository;
import com.example.onlybuns.repository.FollowRepository;
import com.example.onlybuns.repository.PostRepository;

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.example.onlybuns.domain.Follow;

@Service
public class FollowService {
  @Autowired
  private FollowRepository followRepository;

  @Autowired
  private AccountRepository accountRepository;

  private final ConcurrentHashMap<Long, Lock> locks = new ConcurrentHashMap<>();
  // Mapa za broj praćenja po korisniku u poslednjem minutu
  private final ConcurrentHashMap<Long, Integer> followLimitMap = new ConcurrentHashMap<>();

  // Vreme poslednjeg praćenja
  private final ConcurrentHashMap<Long, LocalDateTime> followTimeMap = new ConcurrentHashMap<>();

  public boolean checkIfFollowing(Long followerId, Long followedId) {
      return followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
  }
  // Dohvati sve pratioce za određenog korisnika
  public List<Account> getFollowers(Long followedId) {
    return followRepository.findByFollowedId(followedId);
  }

  @Transactional
  public void followUser(Long followerId, Long followedId) throws InterruptedException {
    Lock lock = locks.get(followedId);

    if (lock == null) {
        lock = new ReentrantLock();
        locks.put(followedId, lock);
    }
    
    lock.lock();
    try {
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("Korisnik ne može pratiti sam sebe");
        }
  
        if (followRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            throw new IllegalArgumentException("Već prati ovog korisnika");
        }
  
        // Provera ograničenja od 50 praćenja po minuti
        followLimitMap.putIfAbsent(followerId, 0);
        followTimeMap.putIfAbsent(followerId, LocalDateTime.now());
  
        LocalDateTime lastFollowTime = followTimeMap.get(followerId);
        int followCount = followLimitMap.get(followerId);
  
        if (lastFollowTime.isBefore(LocalDateTime.now().minusMinutes(1))) {
            // Resetovanje broja praćenja posle jednog minuta
            followTimeMap.put(followerId, LocalDateTime.now());
            followLimitMap.put(followerId, 1);
        } else if (followCount >= 50) {
            throw new IllegalArgumentException("Prekoračen limit od 50 praćenja po minuti");
        } else {
            followLimitMap.put(followerId, followCount + 1);
        }
  
        Account follower = accountRepository.findById(followerId).orElseThrow();
        Account followed = accountRepository.findById(followedId).orElseThrow();
        
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);
  
        followRepository.save(follow);
  
        // Ažuriranje broja pratilaca (Transakcija za konkurentnost)
        followed.setFollowerCount(followed.getFollowerCount() + 1);
        accountRepository.save(followed);
        
        Thread.sleep(500);
    }finally{
        lock.unlock();
        locks.remove(followedId, lock);
    }
  }

  @Transactional
  public void unfollowUser(Long followerId, Long followedId) throws InterruptedException {
    Lock lock = locks.get(followedId);

    if (lock == null) {
        lock = new ReentrantLock();
        locks.put(followedId, lock);
    }

    lock.lock();
    try {
        Follow follow = followRepository.findByFollowerIdAndFollowedId(followerId, followedId)
              .orElseThrow(() -> new IllegalArgumentException("Following Not Found"));

        followRepository.delete(follow);

        Account followed = accountRepository.findById(followedId).orElseThrow();
        followed.setFollowerCount(followed.getFollowerCount() - 1);
        accountRepository.save(followed);
        
        Thread.sleep(500); 
    } finally {
        lock.unlock();
        locks.remove(followedId, lock);
    }
  }
}
