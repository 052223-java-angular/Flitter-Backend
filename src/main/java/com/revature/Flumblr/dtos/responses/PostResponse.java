package com.revature.Flumblr.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.revature.Flumblr.entities.Post;
import com.revature.Flumblr.entities.Comment;

/**
 * The Principal class represents the authenticated user's principal
 * information.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostResponse {
    private String id;

    private String username;

    private String message;

    private String s3Url;

    private String mediaType;

    private Date createTime;

    private Date editTime;

    private List<CommentResponse> comments;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.username = post.getUser().getUsername();
        this.message = post.getMessage();
        this.s3Url = post.getS3Url();
        this.mediaType = post.getMediaType();
        this.createTime = post.getCreateTime();
        this.editTime = post.getEditTime();
        this.comments = new ArrayList<CommentResponse>();
        for (Comment comment : post.getComments()) {
            this.comments.add(new CommentResponse(comment));
        }
    }
}
