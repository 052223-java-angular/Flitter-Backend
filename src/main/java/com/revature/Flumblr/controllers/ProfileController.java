package com.revature.Flumblr.controllers;

import com.revature.Flumblr.dtos.requests.NewProfileRequest;
import com.revature.Flumblr.dtos.responses.ProfileResponse;
import com.revature.Flumblr.services.ProfileService;
import com.revature.Flumblr.services.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/profile")
@AllArgsConstructor
public class ProfileController {

    ProfileService profileService;
    TokenService tokenService;

    @GetMapping("/{id}")
    ResponseEntity<ProfileResponse> readProfileBio(@PathVariable String id,
            @RequestHeader("Authorization") String token) {
        tokenService.validateToken(token, id);

        // include image url and bio in response body
        ProfileResponse res = new ProfileResponse();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // upload profile image
    @PatchMapping("/upload")
    ResponseEntity<?> updateProfileImage(@RequestParam("imgFile") MultipartFile file,
            @RequestBody NewProfileRequest req,
            @RequestHeader("Authorization") String token) throws IOException {

        tokenService.validateToken(token, req.getUserId());
        // return
        // ResponseEntity.status(HttpStatus.OK).body(profileService.setProfileImg(file.getBytes(),
        // req));

        // instead of getting bytes we will get url and save image to s3 bucket
        // profileService.setProfileImg(file.getBytes(), req);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // update profile bio
    @PatchMapping("/bio")
    ResponseEntity<?> updateProfileBio(@RequestBody NewProfileRequest req,
            @RequestHeader("Authorization") String token) {
        tokenService.validateToken(token, req.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(profileService.setBio(req));
    }

}
