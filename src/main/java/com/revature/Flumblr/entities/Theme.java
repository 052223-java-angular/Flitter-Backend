package com.revature.Flumblr.entities;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "themes")
public class Theme {
    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "theme", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Profile> profiles;

}
