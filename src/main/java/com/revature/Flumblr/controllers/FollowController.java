package com.revature.Flumblr.controllers;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.Flumblr.dtos.requests.PotentialFollowerRequest;
import com.revature.Flumblr.dtos.responses.PotentialFollowerResponse;
//import org.springframework.web.bind.annotation.PathVariable;

import com.revature.Flumblr.services.TokenService;
import com.revature.Flumblr.services.FollowService;

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/follows")
public class FollowController {
    // dependency injection ie. services
    private final FollowService followService;
    private final TokenService tokenService;

    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    // username of user followed
    @PostMapping("/{followName}")
    public ResponseEntity<?> createFollow(@RequestHeader("Authorization") String token,
            @PathVariable String followName) {
        String userId = tokenService.extractUserId(token);
        logger.trace(userId + " following " + followName);
        followService.create(userId, followName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // username of user followed
    @DeleteMapping("/{followName}")
    public ResponseEntity<?> deleteFollow(@RequestHeader("Authorization") String token,
            @PathVariable String followName) {
        String userId = tokenService.extractUserId(token);
        logger.trace(userId + " unfollowing " + followName);
        followService.delete(userId, followName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<String>> getFollows(@RequestHeader("Authorization") String token) {
        String userId = tokenService.extractUserId(token);
        logger.trace("getting follows for " + userId);
        return ResponseEntity.status(HttpStatus.OK).body(followService.findAllByUserId(userId));
    }

    @PostMapping("/getFollowers")
    public ResponseEntity<Set<PotentialFollowerResponse>> getPotentialFollowers(
            @RequestHeader("Authorization") String token,
            @RequestBody PotentialFollowerRequest req) {
        tokenService.validateToken(token, req.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(followService.getPotentialListOfFollowers(req.getTagList()));
    }


    @PostMapping("/getUserbyName")
    public ResponseEntity<Set<PotentialFollowerResponse>> getPotentialFollowersbyUsername(
            @RequestHeader("Authorization") String token,
            @RequestBody PotentialFollowerRequest req) {
        tokenService.validateToken(token, req.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(followService.getPotentialListOfFollowersbyName(req.getUsername()));
    }
}
