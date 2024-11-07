package com.example.onlybuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Like;
import com.example.onlybuns.domain.Post;

public interface LikeRepository extends JpaRepository<Like,Long>{
    boolean existsByPostIdAndAccountId(Long postId, Long userId);

    // Metoda za brojanje lajkova za određenu objavu
    long countByPost(Post post);

    // Metoda za proveru da li je korisnik već lajkovao određenu objavu
    boolean existsByPostAndAccount(Post post, Account account);

}
