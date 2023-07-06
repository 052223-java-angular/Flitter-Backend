package com.revature.Flumblr.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.revature.Flumblr.services.TokenService;
import com.revature.Flumblr.services.PostService;
import com.revature.Flumblr.services.S3StorageService;
import com.revature.Flumblr.dtos.requests.NewCommentRequest;
import com.revature.Flumblr.dtos.responses.PostResponse;
import com.revature.Flumblr.services.CommentService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/posts")
public class PostController {
    // dependency injection ie. services
    private final TokenService tokenService;
    private final PostService postService;
    private final CommentService commentService;
    private final S3StorageService s3StorageService;

    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createPost(MultipartHttpServletRequest req, @RequestHeader("Authorization") String token) {

        String userId = tokenService.extractUserId(token);
        logger.trace("creating post from " + userId);

        MultipartFile file = req.getFile("file");

        String fileUrl = null;

        if (file != null) {
            fileUrl = s3StorageService.uploadFile(file);
        }

        postService.createPost(req, fileUrl, userId);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping("/feed/{page}")
    public ResponseEntity<List<PostResponse>> getFeed(@RequestHeader("Authorization") String token,
            @PathVariable int page) {
        String userId = tokenService.extractUserId(token);
        logger.trace("generating feed for " + userId);
        return ResponseEntity.status(HttpStatus.OK).body(postService.getFeed(userId, page - 1));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable String userId,
            @RequestHeader("Authorization") String token) {
        String requesterId = tokenService.extractUserId(token);
        logger.trace("getting posts from " + userId + " requested by " + requesterId);
        return ResponseEntity.status(HttpStatus.OK).body(postService.getUserPosts(userId));
    }

    @GetMapping("/id/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable String postId,
            @RequestHeader("Authorization") String token) {
        String requesterId = tokenService.extractUserId(token);
        logger.trace("getting post " + postId + " requested by " + requesterId);
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId));
    }

    @PostMapping("/comment")
    public ResponseEntity<?> commentOnPost(@RequestBody NewCommentRequest req,
            @RequestHeader("Authorization") String token) {

        tokenService.validateToken(token, req.getUser_id());
        commentService.commentOnPost(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/id/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable String postId,
            @RequestHeader("Authorization") String token) {
        String requesterId = tokenService.extractUserId(token);
        logger.trace("Deleting post " + postId + " requested by " + requesterId);

        String postOwnerId = postService.getPostOwner(postId);

        if (!postOwnerId.equals(requesterId)) {
            logger.warn("User " + requesterId + " attempted to delete post " + postId + " that they do not own");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized to delete this post.");
        }

        postService.deletePost(postId);

        return ResponseEntity.status(HttpStatus.OK).body("Post was successfully deleted.");
    }

}
