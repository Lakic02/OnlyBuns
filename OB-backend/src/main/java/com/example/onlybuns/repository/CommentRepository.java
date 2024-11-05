package com.example.onlybuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onlybuns.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {

}
