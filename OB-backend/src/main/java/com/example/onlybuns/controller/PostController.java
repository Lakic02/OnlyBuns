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
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.onlybuns.domain.Comment;
import com.example.onlybuns.domain.Post;
import com.example.onlybuns.service.PostPublisher;
import com.example.onlybuns.service.PostService;

import io.micrometer.core.annotation.Timed;



@RestController
@RequestMapping("/api/posts")
//@PreAuthorize("hasAuthority('registered')")
public class PostController {
    @Autowired
    private PostService postService;
    
    // @Autowired
    // private RestTemplate restTemplate; // !!! OSTAVITI ZA TESTIRANJE !!!
    @Autowired
    Environment environment;

    @Autowired
    private PostPublisher postPublisher;

    @PostMapping("/publish") //ADMIN USERS
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String publishMessage(@RequestBody Post post) {
        System.out.println("PublishingGGGGGGG message to RabbitMQ...");
        String message = String.format("Description: %s, Time: %s, User: %s",
                post.getDescription(), post.getCreationTime().toString(), post.getAccount().getUserName());
        
        System.out.println("PORUKAAAAAAAAAAAAAAAAA::");
        System.out.println(message);
        postPublisher.sendMessage(message);
        System.out.println("PORUKA POSLATAAAAAAAAAAAAAA");
        return "Message sent: " + message;
    }
    
    @GetMapping("/getAll") //ALL USERS
    public ResponseEntity<List<Post>> getPosts() {
        // String response = restTemplate.getForObject("http://ONLYBUNS/api/posts/internalGetAll", String.class); // !!! OSTAVITI ZA TESTIRANJE !!!
        // System.out.println("Request handled by port: " + environment.getProperty("local.server.port")); // !!! OSTAVITI ZA TESTIRANJE !!!
        System.out.println("GETALLL");// !!! OSTAVITI ZA TESTIRANJE !!!
        return ResponseEntity.ok(postService.getPosts());
    }
    @GetMapping("/findById/{id}") //ALL USERS
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        // String response = restTemplate.getForObject("http://ONLYBUNS/api/posts/internalGetAll", String.class); // !!! OSTAVITI ZA TESTIRANJE !!!
        // System.out.println("Request handled by port: " + environment.getProperty("local.server.port")); // !!! OSTAVITI ZA TESTIRANJE !!!
        System.out.println("FINDBYIDDDD"); // !!! OSTAVITI ZA TESTIRANJE !!!
        Optional<Post> post = postService.findById(id);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    // Dodaj internu metodu za identifikaciju instance
    @GetMapping("/internalGetAll")
    public ResponseEntity<String> internalGetAll() {
        String port = environment.getProperty("local.server.port");
        if ("8082".equals(port)) {
            System.out.println(">>> Request handled on port 8082");
        }
        return ResponseEntity.ok("Handled by port: " + port);
    }

    @GetMapping("/followed/{userId}") //REGISTERED AND ADMIN USERS
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_REGISTERED')")
    public ResponseEntity<List<Post>> getFollowedUsersPosts(@PathVariable Long userId) {
        List<Post> posts = postService.getFollowedUsersPosts(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/likes/count/{postId}") //ALL USERS
    public ResponseEntity<Long> getLikesCount(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(postService.countLikesForPost(post));
    }

    @GetMapping("/comments/{postId}") //ALL USERS
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(postService.getCommentsForPost(post));
    }

    @PutMapping("/update/{postId}") //REGISTERED USERS
    @PreAuthorize("hasAuthority('ROLE_REGISTERED')")
    public ResponseEntity<Post> editPost(
        @PathVariable Long postId,
        @RequestParam(value = "description", required = false) String description,
        @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        Post updatedPost = postService.editPost(postId, description, file);
        return ResponseEntity.ok(updatedPost);
    }
    
    //@Timed(value = "post.creation.time", description = "Time taken to create a post")
    @PostMapping("/create/{accId}") //REGISTERED USERS
    @PreAuthorize("hasAuthority('ROLE_REGISTERED')")
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

    @DeleteMapping("/delete/{postId}") //REGISTERED USERS
    @PreAuthorize("hasAuthority('ROLE_REGISTERED')")
    public ResponseEntity<Post> deletePost(@PathVariable Long postId) {
        Post deletedPost = postService.deletePost(postId);
        return ResponseEntity.ok(deletedPost);
    }

    @PostMapping("/like/{postId}/{userId}") //REGISTERED USERS
    @PreAuthorize("hasAuthority('ROLE_REGISTERED')")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, @PathVariable Long userId) throws InterruptedException {
        postService.addLike(postId, userId);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/dislike/{postId}/{userId}") //REGISTERED USERS
    @PreAuthorize("hasAuthority('ROLE_REGISTERED')")
    public ResponseEntity<Void> dislikePost(@PathVariable Long postId, @PathVariable Long userId) throws InterruptedException {
        postService.removeLike(postId, userId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/hasLiked/{postId}/{userId}") //ALL USERS
    public ResponseEntity<Boolean> hasUserLikedPost(@PathVariable Long postId, @PathVariable Long userId) {
        boolean liked = postService.hasUserLikedPost(postId, userId);
        return ResponseEntity.ok(liked);
    }


    @PostMapping("/comment/{postId}/{userId}") //REGISTERED USERS
    @PreAuthorize("hasAuthority('ROLE_REGISTERED')")
    public ResponseEntity<String> commentOnPost(@PathVariable Long postId, @PathVariable Long userId, @RequestParam("text") String commentTxt) {
        try {
            postService.addComment(postId, userId, commentTxt);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(e.getMessage());  
        }
    }

    @GetMapping("/getFile/{postId}") //ALL USERS
    public ResponseEntity<Map<String, Object>> getFile(@PathVariable Long postId) {
        
        Post post = postService.getPostById(postId);
        Map<String, Object> response = new HashMap<>();
        
        String imagePath = post.getImagePath();
        Integer compress = post.getIsCompressed(); 
        
        response.put("imagePath", imagePath);
        response.put("compress", compress);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/coordinates/{postId}") //ALL USERS
    public String getPostCoordinates(@PathVariable Long postId) {
        return postService.getCoordinatesByPostId(postId);
    }

    @GetMapping("/canComment/{postId}/{userId}") //ALL USERS
    public ResponseEntity<Boolean> createComment(@PathVariable Long postId, @PathVariable Long userId) {
        return ResponseEntity.ok(postService.canComment(postId, userId));
    }
 }
