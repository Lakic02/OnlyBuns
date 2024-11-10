package com.example.onlybuns.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.onlybuns.domain.Comment;
import com.example.onlybuns.domain.Post;
import com.example.onlybuns.service.PostService;



@RestController
@RequestMapping("/api/posts")
@PreAuthorize("hasAuthority('registered')")
public class PostController {
    @Autowired
    private PostService postService;
    
    @GetMapping("/getAll")
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok(postService.getPosts());
    }
    @GetMapping("/findById/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.findById(id);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/likes/count/{postId}")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(postService.countLikesForPost(post));
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(postService.getCommentsForPost(post));
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<Post> editPost(
        @PathVariable Long postId,
        @RequestParam(value = "description", required = false) String description,
        @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        Post updatedPost = postService.editPost(postId, description, file);
        return ResponseEntity.ok(updatedPost);
    }

    @PostMapping("/create/{accId}")
    public ResponseEntity<Post> createPost(
        @RequestParam("description") String description,
        @RequestParam("latitude") Double latitude,
        @RequestParam("longitude") Double longitude,
        @RequestParam("file") MultipartFile file, 
        @PathVariable Long accId) throws IOException {

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
        String uploadDir = System.getProperty("user.dir") + "/images/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String filePath = uploadDir + fileName;
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);

        Post post = postService.createPost(description, latitude, longitude, fileName, accId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Post> deletePost(@PathVariable Long postId) {
        Post deletedPost = postService.deletePost(postId);
        return ResponseEntity.ok(deletedPost);
    }

    @PostMapping("/like/{postId}/{userId}")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, @PathVariable Long userId) throws InterruptedException {
        postService.addLike(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{postId}/{userId}")
    public ResponseEntity<Void> commentOnPost(@PathVariable Long postId, @PathVariable Long userId, @RequestBody Comment comment) {
        postService.addComment(postId, userId, comment);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getFile/{postId}")
    public ResponseEntity<Map<String, Object>> getFile(@PathVariable Long postId) {
        
        Post post = postService.getPostById(postId);
        Map<String, Object> response = new HashMap<>();
        
        String imagePath = post.getImagePath();
        Integer compress = post.getIsCompressed(); 
        
        response.put("imagePath", imagePath);
        response.put("compress", compress);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/coordinates/{postId}")
    public String getPostCoordinates(@PathVariable Long postId) {
        return postService.getCoordinatesByPostId(postId);
    }
    
}
