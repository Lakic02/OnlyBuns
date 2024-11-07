package com.example.onlybuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onlybuns.domain.Post;
import java.util.*;

public interface PostRepository extends JpaRepository<Post,Long>{
    // Metoda za pronalaženje posta po ID-u koji nije logički obrisan
    Optional<Post> findByIdAndIsDeletedFalse(Long id);
    List<Post> findAllByIsDeletedFalse();
}
