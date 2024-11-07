package com.example.onlybuns.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "Follow")
public class Follow {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "follower_id", nullable = false)
  private Account follower;

  @ManyToOne
  @JoinColumn(name = "followed_id", nullable = false)
  private Account followed;

  // Getters and Setters
  public Long getId() {
      return id;
  }

  public void setId(Long id) {
      this.id = id;
  }

  public Account getFollower() {
      return follower;
  }

  public void setFollower(Account follower) {
      this.follower = follower;
  }

  public Account getFollowed() {
      return followed;
  }

  public void setFollowed(Account followed) {
      this.followed = followed;
  }
}
