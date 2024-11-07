package com.example.onlybuns.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    private static final ConcurrentHashMap<Long, Lock> locks = new ConcurrentHashMap<>();
    
    @Transactional
    public List<Post> getPosts(){
        return postRepository.findAllByIsDeletedFalse();
    }
    // Brojanje lajkova za objavu
    @Transactional
    public long countLikesForPost(Post post) {
        return likeRepository.countByPost(post);
    }
    // Dobijanje svih komentara za objavu
    @Transactional
    public List<Comment> getCommentsForPost(Post post) {
        return commentRepository.findByPost(post);
    }


    @Transactional
    public Post createPost(String description, Double latitude, Double longitude, MultipartFile file, Long accId) throws IOException {
    Account account = accountRepository.findById(accId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

            System.out.println("NIJEEEEEEEEEEEEEEE");
    Post post = new Post();
    post.setDescription(description);
    post.setLatitude(latitude);
    post.setLongitude(longitude);
    post.setAccount(account); // Povezivanje sa korisnikom
    post.setImage(file.getBytes()); // Preuzimanje fajla u byte array
    post.setIsDeleted(false);
    post.setCreationTime(LocalDateTime.now());
        
    System.out.println("JESTEEEEEEEEEEE");
    // Spremanje posta u bazu podataka
    return postRepository.save(post);
    }

    @Transactional
    public Post editPost(Long postId, String newDescription) {
        Optional<Post> optionalPost = postRepository.findByIdAndIsDeletedFalse(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setDescription(newDescription);
            post.setCreationTime(LocalDateTime.now());
            return postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found or has been deleted");
        }
    }

    @Transactional
    public Post deletePost(Long postId) {
        Optional<Post> optionalPost = postRepository.findByIdAndIsDeletedFalse(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setIsDeleted(true);
            return postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found or has been deleted");
        }
    }


    @Transactional
    public void addLike(Long postId, Long userId) {
        Lock lock = locks.get(postId);

        if (lock == null) {
            lock = new ReentrantLock();
            locks.put(postId, lock);
        }

        lock.lock();
        try {
            boolean exists = likeRepository.existsByPostIdAndAccountId(postId, userId);
            if (exists) {
                throw new RuntimeException("User has already liked this post.");
            }
            Like like = new Like();
            like.setPost(postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId)));
            like.setAccount(accountRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId)));
            like.setCreationTime(LocalDateTime.now());

            likeRepository.save(like);

        } finally {
            lock.unlock();
            locks.remove(postId, lock);
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
    @Transactional
    public Post getPostById(Long id) {
       return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found with ID: " + id));
    }

    // Metoda za konverziju adrese u koordinate
    //public Location geocode(String address) {
        // Implementacija pomoću eksternog servisa (npr. Google Maps API)
   // }
}
