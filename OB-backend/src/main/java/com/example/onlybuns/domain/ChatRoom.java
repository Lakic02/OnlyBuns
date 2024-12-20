package com.example.onlybuns.domain;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_room")
public class ChatRoom {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "admin_id", nullable = false)
  private Account admin;

  @Column(name = "room_name", nullable = false, unique = true)
  private String roomName;

  @OneToMany(mappedBy = "chatRoom")
  @JsonIgnore
  private Set<ChatRoomMembership> memberships = new HashSet<>();
  // @ManyToMany
  // @JoinTable(
  //   name = "chat_room_members",
  //   joinColumns = @JoinColumn(name = "chat_room_id"),
  //   inverseJoinColumns = @JoinColumn(name = "account_id")
  // )
  // private Set<Account> members;

  // Getteri i setteri
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }
  
  public Account getAdmin() {
    return admin;
  }

  public void setAdmin(Account admin) {
    this.admin = admin;
  }

  public Set<ChatRoomMembership> getMemberships() {
    return memberships;
  }

  public void setMemberships(Set<ChatRoomMembership> memberships) {
    this.memberships = memberships;
  }
  // public Set<Account> getMembers() {
  //   return members;
  // }

  // public void setMembers(Set<Account> members) {
  //   this.members = members;
  // }
}