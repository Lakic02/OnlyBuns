package com.example.onlybuns.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
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
import net.coobird.thumbnailator.Thumbnails;

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
    public Post createPost(String description, Double latitude, Double longitude, String file, Long accId) throws IOException {
    Account account = accountRepository.findById(accId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

            Post post = new Post();
            post.setDescription(description);
            post.setLatitude(latitude);
            post.setLongitude(longitude);
            post.setAccount(account); 
            post.setImagePath(file); 
            post.setDeleted(false);
            post.setCreationTime(LocalDateTime.now());
            post.setCompressTime(LocalDateTime.now());
            post.setIsCompressed(0);
                
           
            return postRepository.save(post);
    }

    @Transactional
    public Post editPost(Long postId, String newDescription) {
        Optional<Post> optionalPost = postRepository.findByIdAndIsDeletedFalse(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setDescription(newDescription);
            post.setCreationTime(LocalDateTime.now());
            post.setCompressTime(LocalDateTime.now());
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
            post.setDeleted(true);
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

    @Transactional
    public Post editPost(Long postId, String newDescription, MultipartFile newFile) throws IOException {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
        Optional<Post> optionalPost = postRepository.findByIdAndIsDeletedFalse(postId);
        if (optionalPost.isEmpty()) {
            throw new RuntimeException("Post not found or has been deleted");
        }

        Post post = optionalPost.get();

        // Ažuriranje opisa
        if (newDescription != null && !newDescription.isEmpty()) {
            post.setDescription(newDescription);
        }

        // Ažuriranje slike
        if (newFile != null && !newFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + newFile.getOriginalFilename();
           
            String uploadDir = System.getProperty("user.dir") + "/images/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdir();
            }
            String filePath = uploadDir + fileName;
            File destinationFile = new File(filePath);
            newFile.transferTo(destinationFile);

            // Brisanje stare slike (opciono)
            String oldImagePath = System.getProperty("user.dir") + "/images/" + post.getImagePath();
            File oldImageFile = new File(oldImagePath);
            if (oldImageFile.exists()) {
                oldImageFile.delete();
            }
            post.setIsCompressed(0);
            post.setImagePath(fileName);
        }

        post.setCreationTime(LocalDateTime.now());
        return postRepository.save(post);
    }

    @Transactional
    @Scheduled(cron = "0 * * * * ?")
    public void compressOldImages() throws IOException {
    
        List<Post> posts = postRepository.findAll();
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
    
        List<Post> oldPosts = posts.stream()
            .filter(post -> post.getCompressTime().isBefore(oneMonthAgo) && post.getImagePath() != null && post.getIsCompressed() == 0)
            .collect(Collectors.toList());
        for (Post post : oldPosts) {
            String imagePath = post.getImagePath();
            
            String imageNameWithoutExtension = imagePath.substring(0, imagePath.lastIndexOf('.'));
            String newImagePath = imageNameWithoutExtension + ".jpg";
            
            String imageFullPath = System.getProperty("user.dir") + "/compressedImages/" + newImagePath;
            String imgFullPath = System.getProperty("user.dir") + "/images/" + imagePath;
            File imgFile = new File(imgFullPath);
    
            BufferedImage image = ImageIO.read(imgFile);
            int width = image.getWidth();
            int height = image.getHeight();
    
            if (width > 800 || height > 800) {
                Thumbnails.of(imgFile)
                    .size(800, 800)
                    .outputQuality(0.5)
                    .outputFormat("jpg")
                    .toFile(imageFullPath);
    
                post.setCompressTime(LocalDateTime.now());
                post.setImagePath(newImagePath);
                post.setIsCompressed(1);
                postRepository.save(post);
            }
        }
    }

    @Cacheable(value = "coordinatesCache", key = "#postId")
        public String getCoordinatesByPostId(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        return "Longitude: " + post.getLongitude() + ", Latitude: " + post.getLatitude();
    }
}
