package com.revature.Flumblr.services;

import com.revature.Flumblr.entities.Profile;
import com.revature.Flumblr.entities.Tag;
import com.revature.Flumblr.entities.Theme;
import com.revature.Flumblr.entities.User;
import com.revature.Flumblr.repositories.ProfileRepository;
import com.revature.Flumblr.repositories.TagRepository;
import com.revature.Flumblr.repositories.ThemeRepository;
import com.revature.Flumblr.repositories.UserRepository;
import com.revature.Flumblr.utils.custom_exceptions.BadRequestException;
import com.revature.Flumblr.utils.custom_exceptions.FileNotUploadedException;
import com.revature.Flumblr.utils.custom_exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ThemeRepository themeRepository;
    private final TagRepository tagRepository;

    public Profile getProfileByUserId(String id) {
        User existingUser = userRepository.getReferenceById(id);
        return profileRepository.getProfileByUser(existingUser);
    }

    public void setProfileImg(String profileId, String URL) {
        if(URL == null) {
            throw new FileNotUploadedException("An error occurred uploading the profile image.");
        }
        profileRepository.setProfileImg(profileId, URL);
    }

    public void setBio(String profileId, String bio) {
        if(bio == null) {
            throw new BadRequestException("Cannot submit a null bio.");
        }
        profileRepository.setBio(profileId, bio);
    }

    public void setTheme(String profileId, String themeName) {
        Theme theme = themeRepository.findByName(themeName);
        if(theme == null) {
            throw new ResourceNotFoundException("No theme with name: " + themeName + " found.");
        }
        profileRepository.setTheme(profileId, theme);
    }

    public Profile findById(String id) {
        Optional<Profile> opt = profileRepository.findById(id);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException("Unable to find profile ID: " + id);
        }
        return opt.get();
    }

   public Profile assignTagToProfile(String profileId, String tagName) {
        Set<Tag> tagSet = null;
        Profile profile = profileRepository.findById(profileId).get();
        Tag tag = tagRepository.findByName(tagName).get();
        tagSet = profile.getTags();
        if(tagSet.size() >= 5) {
            throw new BadRequestException("A profile cannot store more than five (5) tags at a time.");
        }
        tagSet.add(tag);
        profile.setTags(tagSet);
        return profileRepository.save(profile);
   }

   public Set<Tag> getTagsByProfile(String profileId) {
        return profileRepository.findById(profileId).get().getTags();
   }

   public void deleteTagsFromProfile(String profileId, Tag tag) {
        //???
        profileRepository.findById(profileId).get().getTags().remove(tag);
   }


}
