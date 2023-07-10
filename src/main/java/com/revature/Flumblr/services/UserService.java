package com.revature.Flumblr.services;

import com.revature.Flumblr.entities.Profile;
import com.revature.Flumblr.entities.Theme;
import com.revature.Flumblr.repositories.ProfileRepository;

import com.revature.Flumblr.repositories.ThemeRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.revature.Flumblr.dtos.requests.NewLoginRequest;
import com.revature.Flumblr.dtos.requests.NewUserRequest;
import com.revature.Flumblr.dtos.responses.Principal;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import com.revature.Flumblr.repositories.UserRepository;
import com.revature.Flumblr.repositories.VerifivationRepository;
import com.revature.Flumblr.utils.custom_exceptions.ResourceConflictException;
import com.revature.Flumblr.utils.custom_exceptions.ResourceNotFoundException;
import com.revature.Flumblr.entities.User;
import com.revature.Flumblr.entities.Verification;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ProfileRepository profileRepository;
    private final ThemeRepository themeRepository;
    private final VerifivationRepository verifivationRepository;
    private final VerificationService verificationService;


    // private final PostService postService;

    public User registerUser(NewUserRequest req) {
        String hashed = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());

        
        // verifying user email
        User existingUseer = userRepository.findByEmailIgnoreCase(req.getEmail());

        //check if email is a from a valid domain

        boolean bool = verificationService.isValidEmailAddress(req.getEmail());

        if(bool == false){
            throw new ResourceConflictException("Email address is not valid");
        }


        if(existingUseer != null){
            throw new ResourceConflictException("Email address is alrady being used!");
        }    

        User newUser = new User(req.getUsername(), hashed, req.getEmail(), roleService.getByName("USER"));

        // save and return user
        User createdUser = userRepository.save(newUser);

        // create new verification data based on Verification table based on new user 
        Verification verification = new Verification(createdUser);
        verifivationRepository.save(verification);

        //send email to the user on the new email they entered
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(req.getEmail());
        mailMessage.setSubject("Complete Registration!");
        //mailMessage.setFrom("YOUR EMAIL ADDRESS");
        mailMessage.setText("To confirm your account, please click here : " + 
                            "http://localhost:5000/flumblr/api/auth/verify-account?token=" +  verification.getVerificationToken());

        verificationService.sendEmail(mailMessage);

        // create and save unique profile id for each user to be updated on profile page
        // set profile_img to a default silhouette in s3 bucket - once uploaded add url as a default
        Profile blankProfile = new Profile(createdUser, "https://flumblr.s3.amazonaws.com/879fbd85-d8c1-43c6-a31a-de78c04b3918-profile.jpg", "", themeRepository.findByName("default"));

        profileRepository.save(blankProfile);

        return createdUser;
    }

    public User findById(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty())
            throw new ResourceNotFoundException("couldn't find user for id " + userId);
        return userOpt.get();
    }

    public User findByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty())
            throw new ResourceNotFoundException("couldn't find user for username " + username);
        return userOpt.get();
    }

    public Principal login(NewLoginRequest req) {
        Optional<User> userOpt = userRepository.findByUsername(req.getUsername());

        if (userOpt.isPresent()) {
            User foundUser = userOpt.get();
            if (BCrypt.checkpw(req.getPassword(), foundUser.getPassword())) {
                return new Principal(foundUser);
            } else {
                throw new ResourceConflictException("Invalid password");
            }
        }

        throw new ResourceNotFoundException("Invalid username");
    }

    public boolean isValidUsername(String username) {
        return username.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
    }

    public boolean isUniqueUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.isEmpty();
    }

    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }

    public boolean isSamePassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public boolean usernameExists(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return true;
        } else {
            return false;
        }
    }
}
