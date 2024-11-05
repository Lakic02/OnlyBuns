package com.example.onlybuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onlybuns.domain.Post;

public interface PostRepository extends JpaRepository<Post,Long>{

}
