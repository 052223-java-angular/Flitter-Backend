package com.revature.Flumblr.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

import com.revature.Flumblr.entities.Comment;

/**
 * The Principal class represents the authenticated user's principal
 * information.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentResponse {
    private String username;

    private String comment;

    private Date createTime;

    private Date editTime;

    private String postId;

    CommentResponse(Comment comment) {
        this.username = comment.getUser().getUsername();
        this.comment = comment.getComment();
        this.createTime = comment.getCreateTime();
        this.editTime = comment.getEditTime();
        this.postId = comment.getPost().getId();
    }
}
