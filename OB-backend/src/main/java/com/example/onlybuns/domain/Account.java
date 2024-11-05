package com.example.onlybuns.domain;


import jakarta.persistence.*;

@Entity
@Table(name = "Account")
public class Account {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    @Column(name="name", nullable = false)
    public String name;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    
    public enum Role {
        admin,
        registered,
        unauthenticated
    }

}
