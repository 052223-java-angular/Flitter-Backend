package com.revature.Flumblr.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.Flumblr.entities.Theme;

public interface ThemeRepository extends JpaRepository<Theme, String> {
    Optional<Theme> findByName(String name);

    List<Theme> findAllByOrderByIdAsc();

}
