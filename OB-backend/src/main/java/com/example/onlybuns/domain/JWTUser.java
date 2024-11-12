package com.example.onlybuns.domain;

public class JWTUser {
	public long id;
	public String username;
	public String role;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public JWTUser() {
		super();
	}
	public JWTUser(long id, String username, String role) {
		super();
		this.id = id;
		this.username = username;
		this.role = role;
	}
	@Override
	public String toString() {
		return "JWTUser [id=" + id + ", username=" + username + ", role=" + role + "]";
	}
}
