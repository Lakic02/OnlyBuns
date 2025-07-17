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
    // if (followerId.equals(followedId)) {
    //     throw new IllegalArgumentException("Korisnik ne može pratiti sam sebe");
    // }

    // // Provera ograničenja od 50 praćenja po minuti
    // followLimitMap.putIfAbsent(followerId, 0);
    // followTimeMap.putIfAbsent(followerId, LocalDateTime.now());

    // LocalDateTime lastFollowTime = followTimeMap.get(followerId);
    // int followCount = followLimitMap.get(followerId);

    // if (lastFollowTime.isBefore(LocalDateTime.now().minusMinutes(1))) {
    //     // Resetovanje broja praćenja posle jednog minuta
    //     followTimeMap.put(followerId, LocalDateTime.now());
    //     followLimitMap.put(followerId, 1);
    // } else if (followCount >= 50) {
    //     throw new IllegalArgumentException("Prekoračen limit od 50 praćenja po minuti");
    // } else {
    //     followLimitMap.put(followerId, followCount + 1);
    // }

    // // Zaključavanje reda za korisnika koga prati
    // Account followed = accountRepository.findByIdWithLock(followedId)
    //     .orElseThrow(() -> new RuntimeException("Korisnik za praćenje nije pronađen"));

    // // Provera da li korisnik već prati ovog korisnika
    // if (followRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
    //     throw new IllegalArgumentException("Već prati ovog korisnika");
    // }

    // // Pronalaženje korisnika koji prati
    // Account follower = accountRepository.findById(followerId)
    //     .orElseThrow(() -> new RuntimeException("Korisnik koji prati nije pronađen"));
    
    // // Kreiranje nove veze praćenja
    // Follow follow = new Follow();
    // follow.setFollower(follower);
    // follow.setFollowed(followed);
    // follow.setFollowDate(LocalDateTime.now());

    // // Čuvanje veze praćenja i ažuriranje broja pratilaca
    // followRepository.save(follow);
    // followed.setFollowerCount(followed.getFollowerCount() + 1);
    // accountRepository.save(followed);

    // // Opciona simulacija konkurentnog pristupa
    // Thread.sleep(5000);
    // //Thread.sleep(10000);

    String threadName = Thread.currentThread().getName();
    System.out.println("[" + threadName + "] Pokusava da pristupi metodi followUser()");

    if (followerId.equals(followedId)) {
        System.out.println("[" + threadName + "] Korisnik ne moze pratiti sam sebe");
        throw new IllegalArgumentException("Korisnik ne moze pratiti sam sebe");
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
        System.out.println("[" + threadName + "] Prekoracen limit od 50 pracenja po minuti");
        throw new IllegalArgumentException("Prekoracen limit od 50 pracenja po minuti");
    } else {
        followLimitMap.put(followerId, followCount + 1);
    }

    // Zaključavanje reda za korisnika koga prati
    Account followed = accountRepository.findByIdWithLock(followedId)
        .orElseThrow(() -> new RuntimeException("Korisnik za pracenje nije pronadjen"));
    System.out.println("[" + threadName + "] Zakljucao korisnika sa ID: " + followedId);

    // Provera da li korisnik već prati ovog korisnika
    boolean exists = followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    if (exists) {
        System.out.println("[" + threadName + "] Korisnik vec prati ovog korisnika");
        throw new IllegalArgumentException("Vec prati ovog korisnika");
    }

    // Pronalaženje korisnika koji prati
    Account follower = accountRepository.findById(followerId)
        .orElseThrow(() -> new RuntimeException("Korisnik koji prati nije pronadjen"));

    // Kreiranje nove veze praćenja
    Follow follow = new Follow();
    follow.setFollower(follower);
    follow.setFollowed(followed);
    follow.setFollowDate(LocalDateTime.now());

    followRepository.save(follow);
    System.out.println("[" + threadName + "] Sacuvao vezu pracenja izmedju korisnika " + followerId + " i " + followedId);

    followed.setFollowerCount(followed.getFollowerCount() + 1);
    accountRepository.save(followed);
    System.out.println("[" + threadName + "] Azurirao broj pratilaca za korisnika sa ID: " + followedId);

    System.out.println("[" + threadName + "] Uspavljivanje niti na 5 sekundi...");
    Thread.sleep(5000);
    System.out.println("[" + threadName + "] Zavrsio metodu followUser()");
  }

  @Transactional
  public void unfollowUser(Long followerId, Long followedId) throws InterruptedException {
    // Zaključavanje reda za korisnika koga prati
    Account followed = accountRepository.findByIdWithLock(followedId)
        .orElseThrow(() -> new RuntimeException("Korisnik za pracenje nije pronadjen"));

    // Pronalaženje veze praćenja
    Follow follow = followRepository.findByFollowerIdAndFollowedId(followerId, followedId)
        .orElseThrow(() -> new IllegalArgumentException("Pracenje nije pronadjeno"));

    // Brisanje veze praćenja
    followRepository.delete(follow);

    // Ažuriranje broja pratilaca
    followed.setFollowerCount(followed.getFollowerCount() - 1);
    accountRepository.save(followed);

    // Opciona simulacija konkurentnog pristupa
    Thread.sleep(5000);
    //Thread.sleep(10000);
  }
}
