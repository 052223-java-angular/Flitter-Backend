package com.revature.Flumblr.services;

import org.springframework.stereotype.Service;

import com.revature.Flumblr.dtos.requests.NewLoginRequest;
import com.revature.Flumblr.dtos.requests.NewUserRequest;
import com.revature.Flumblr.dtos.responses.Principal;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

// import com.revature.Flumblr.repositories.RoleRepository;
import com.revature.Flumblr.repositories.UserRepository;
import com.revature.Flumblr.utils.custom_exceptions.UserNotFoundException;
import com.revature.Flumblr.entities.User;
import lombok.AllArgsConstructor;
// import com.revature.Flumblr.entities.Role;
@Service
@AllArgsConstructor
public class UserService {
    
    private final UserRepository userRepo;
   
    private final RoleService roleService;

    public User registerUser(NewUserRequest req) {
        String hashed = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());

        
    
        User newUser = new User(req.getUsername(), hashed, req.getEmail(), roleService.getByName("USER"));
        // save and return user
        return userRepo.save(newUser);
    }

    public User findById(String userId) {
        Optional<User> userOpt = userRepo.findById(userId);
        if(userOpt.isEmpty()) throw new UserNotFoundException("couldn't find user for id " + userId);
        return userOpt.get();
    }

   public Principal login(NewLoginRequest req) {
        Optional<User> userOpt = userRepo.findByUsername(req.getUsername());

        if (userOpt.isPresent()) {
            User foundUser = userOpt.get();
            if (BCrypt.checkpw(req.getPassword(), foundUser.getPassword())) {
                return new Principal(foundUser);
            } else {
                throw new UserNotFoundException("Invalid password");
            }
        }

        throw new UserNotFoundException("Invalid username");
    }

    public boolean isValidUsername(String username) {
        return username.matches("^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$");
    }
  
    public boolean isUniqueUsername(String username) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        return userOpt.isEmpty();
    }

    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }

    public boolean isSamePassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public boolean usernameExists(String username) {
          Optional<User> userOpt = userRepo.findByUsername(username);
          if (userOpt.isPresent()) {
            return true;
          } else {
            return false;
          }
    }

}
