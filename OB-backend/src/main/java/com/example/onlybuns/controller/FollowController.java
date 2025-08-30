package com.example.onlybuns.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.service.FollowService;

@RestController
@RequestMapping("/api/follow")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_REGISTERED')")
public class FollowController {
  @Autowired
  private FollowService followService;

  @GetMapping("/status/{followerId}/{followedId}")
  public ResponseEntity<Boolean> checkIfFollowing(@PathVariable Long followerId, @PathVariable Long followedId) {
    boolean isFollowing = followService.checkIfFollowing(followerId, followedId);
    return new ResponseEntity<>(isFollowing, HttpStatus.OK);
  }
  //Ko mene prati
  @GetMapping("/followers/{followedId}")
  public ResponseEntity<List<Account>> getFollowers(@PathVariable Long followedId) {
    List<Account> followers = followService.getFollowers(followedId);
    return new ResponseEntity<>(followers, HttpStatus.OK);
  }

  @PostMapping("/{followerId}/{followedId}")
  public ResponseEntity<?> followUser(@PathVariable Long followerId, @PathVariable Long followedId) throws InterruptedException {
      followService.followUser(followerId, followedId);
      return ResponseEntity.ok("User successfully followed");
  }

  @DeleteMapping("/{followerId}/{followedId}")
  public ResponseEntity<?> unfollowUser(@PathVariable Long followerId, @PathVariable Long followedId) throws InterruptedException {
      followService.unfollowUser(followerId, followedId);
      return ResponseEntity.ok("User successfully unfollowed");
  }

  @GetMapping("/getFollowing/{userId}")
  public ResponseEntity<List<Account>> getFollowing(@PathVariable Long userId) {
    System.out.println("ALOOOOO: " + userId);
    List<Account> following = followService.getFollowing(userId);
    System.out.println("Following accounts for user " + userId + ": " + following);
    return new ResponseEntity<>(following, HttpStatus.OK);
  }
}
