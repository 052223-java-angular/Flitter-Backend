package com.revature.Flumblr.entities;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "profile")
public class Profile {
    @Id
    private String id;

    @Column
    private String profile_img;

    @Column
    private String bio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    @JsonBackReference
    private Theme theme;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinTable(name = "profile_tag_list", joinColumns = @JoinColumn(name = "profile_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    public Profile(User user, String profile_img, String bio, Theme theme) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.profile_img = profile_img;
        this.bio = bio;
        this.theme = theme;
    }

}
