package com.example.onlybuns.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "Follow")
public class Follow {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "follower_id", nullable = false) //prati
  private Account follower;

  @ManyToOne
  @JoinColumn(name = "followed_id", nullable = false) //je zapracen
  private Account followed;

  @Column(name="followed_date")
  private LocalDateTime followDate;

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
  public LocalDateTime getFollowDate() {
    return followDate;
}

public void setFollowDate(LocalDateTime followDate) {
    this.followDate = followDate;
}

}
