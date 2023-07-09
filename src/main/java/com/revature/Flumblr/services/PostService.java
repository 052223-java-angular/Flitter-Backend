package com.revature.Flumblr.services;

import com.revature.Flumblr.repositories.PostRepository;
import com.revature.Flumblr.repositories.UserRepository;
import com.revature.Flumblr.utils.custom_exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import com.revature.Flumblr.dtos.responses.PostResponse;
import com.revature.Flumblr.entities.User;

import lombok.AllArgsConstructor;

import com.revature.Flumblr.entities.Follow;
import com.revature.Flumblr.entities.Post;
import com.revature.Flumblr.entities.Tag;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TagService tagService;

    public List<PostResponse> getFeed(String userId, int page) {
        User user = userService.findById(userId);
        List<User> following = new ArrayList<User>();
        for (Follow follow : user.getFollows()) {
            following.add(follow.getFollow());
        }
        List<Post> posts = postRepository.findAllByUserIn(following,
                PageRequest.of(page, 20, Sort.by("createTime").descending()));
        List<PostResponse> resPosts = new ArrayList<PostResponse>();
        for (Post userPost : posts) {
            resPosts.add(new PostResponse(userPost));
        }
        return resPosts;
    }

    public List<PostResponse> getUserPosts(String userId) {
        List<Post> userPosts = this.postRepository.findByUserIdOrderByCreateTimeDesc(userId);
        List<PostResponse> resPosts = new ArrayList<PostResponse>();
        for (Post userPost : userPosts) {
            resPosts.add(new PostResponse(userPost));
        }
        return resPosts;
    }

    public PostResponse getPost(String postId) {
        Optional<Post> userPost = this.postRepository.findById(postId);
        if (userPost.isEmpty())
            throw new ResourceNotFoundException("Post(" + postId + ") Not Found");
        return new PostResponse(userPost.get());
    }

    public Post findById(String postId) {
        Optional<Post> userPost = this.postRepository.findById(postId);
        if (userPost.isEmpty())
            throw new ResourceNotFoundException("Post(" + postId + ") Not Found");
        return userPost.get();
    }
    public String getPostOwner(String postId) {
    Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post with id " + postId + " was not found"));
    return post.getUser().getId();
    }

    public void deletePost(String postId) {
    try {
        postRepository.deleteById(postId);
    } catch (EmptyResultDataAccessException e) {
        throw new ResourceNotFoundException("Post with id " + postId + " was not found");
    }
    }


    public void createPost(MultipartHttpServletRequest req, String fileUrl, String userId) {

        Optional<User> userOpt = userRepository.findById(userId);

        User user = userOpt.get();
        
        String message = req.getParameter("message");
        
        String mediaType = req.getParameter("mediaType");

        String[] tagsArray = req.getParameterValues("tags");

        Set<Tag> tagsList = new HashSet<>();
    

        for(String tagNames: tagsArray){

            System.out.println(tagNames);

            Tag tag = tagService.findByName(tagNames);

            tagsList.add(tag); 
        }

        Post post = new Post(message, mediaType, fileUrl, user, tagsList);
        
        postRepository.save(post);



    }   

}
