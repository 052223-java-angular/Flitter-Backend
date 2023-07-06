package com.revature.Flumblr.services;

import org.springframework.stereotype.Service;

import com.revature.Flumblr.dtos.requests.ProfileVoteRequest;
import com.revature.Flumblr.entities.Post;
import com.revature.Flumblr.entities.Profile;
import com.revature.Flumblr.entities.ProfileVote;
import com.revature.Flumblr.entities.User;
import com.revature.Flumblr.repositories.ProfileVoteRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProfileVoteService {
    ProfileVoteRepository profileVoteRepository;
    ProfileService profileService;
    UserService userService;

    public void vote(ProfileVoteRequest req) {
        User user = userService.findById(req.getUserId());
        Profile profile = profileService.findById(req.getProfileId());

        ProfileVote vote = findByUserAndProfile(user, profile);
        if (vote == null) {
            vote = new ProfileVote(user, profile, req.isVote());
        } else {
            if (vote.isVote() == req.isVote()) {
                delete(vote);
                return;
            } else {
                vote.setVote(req.isVote());
            }
        }
        save(vote);
    }

    public void save(ProfileVote vote) {
        profileVoteRepository.save(vote);
    }

    public ProfileVote findById(String id) {
        return profileVoteRepository.findById(id).orElse(null);
    }

    public void delete(ProfileVote vote) {
        profileVoteRepository.delete(vote);
    }

    public ProfileVote findByUserAndProfile(User user, Profile profile) {
        return profileVoteRepository.findByUserAndProfile(user, profile).orElse(null);
    }
}