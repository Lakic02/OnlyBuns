package com.example.onlybuns.domain;


import jakarta.persistence.*;

@Entity
@Table(name = "Account")
public class Account {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    public String firstName;
    public String lastName;

    @Column(name = "userName", nullable = false)
    public String userName;
    @Column(name ="email",nullable = false,unique = true)
    public String email;
    @Column(name = "password", nullable = false)
    public String password;
    @Column(name = "address", nullable = false)
    public String address;
    //@Column(name = "role", nullable = false)
    //private String role;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    public Role role;
    
    @Column(name = "follower_count")
    private int followerCount = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    
    public enum Role {
        admin,
        registered,
        unauthenticated
    }

}
