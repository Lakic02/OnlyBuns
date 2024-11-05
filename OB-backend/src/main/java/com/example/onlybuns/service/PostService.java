package com.example.onlybuns.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.onlybuns.domain.Account;
import com.example.onlybuns.domain.Comment;
import com.example.onlybuns.domain.Like;
import com.example.onlybuns.domain.Post;
import com.example.onlybuns.repository.AccountRepository;
import com.example.onlybuns.repository.CommentRepository;
import com.example.onlybuns.repository.LikeRepository;
import com.example.onlybuns.repository.PostRepository;

import jakarta.transaction.Transactional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public Post createPost(Post post) {
        post.setCreationTime(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    public List<Post> getPosts(){
        return postRepository.findAll();
    }

    @Transactional
    public void addLike(Long postId, Long userId) {
        Like like = new Like();
        like.setPost(postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId)));
        like.setAccount(accountRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId)));
        like.setCreationTime(LocalDateTime.now());

        likeRepository.save(like);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }

    @Transactional
    public Comment addComment(Long postId, Long userId, Comment comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        comment.setPost(post);
        comment.setAccount(account);
        comment.setCreationTime(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    // Metoda za konverziju adrese u koordinate
    //public Location geocode(String address) {
        // Implementacija pomoću eksternog servisa (npr. Google Maps API)
   // }
}
