package com.revature.Flumblr.services;

import com.revature.Flumblr.dtos.requests.NewProfileRequest;
import com.revature.Flumblr.entities.Profile;
import com.revature.Flumblr.entities.User;
import com.revature.Flumblr.repositories.ProfileRepository;
import com.revature.Flumblr.repositories.UserRepository;
import com.revature.Flumblr.utils.custom_exceptions.ProfileNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class ProfileService {

    UserRepository userRepository;
    ProfileRepository profileRepository;

    public Profile findById(String profileId) {
        Optional<Profile> profileOpt = profileRepository.findById(profileId);
        if (profileOpt.isEmpty()) {
            throw new ProfileNotFoundException("No profile located for specified Id.");
        }
        return profileOpt.get();
    }

    public void setProfileImg(String img, NewProfileRequest req) {
        User existingUser = userRepository.getReferenceById(req.getUserId());
        profileRepository.setProfileImg(img, existingUser);
    }

    public Profile setBio(NewProfileRequest req) {
        User existingUser = userRepository.getReferenceById(req.getUserId());
        return profileRepository.setBio(req.getBio(), existingUser);
    }

    public Profile getProfileByUser(String id) {

        User existingUser = userRepository.getReferenceById(id);

        return profileRepository.getProfileByUser(existingUser);
    }

}
