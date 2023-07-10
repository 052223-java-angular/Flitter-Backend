package com.revature.Flumblr.repositories;

import com.revature.Flumblr.dtos.responses.PotentialFollowerResponse;
import com.revature.Flumblr.entities.Profile;
import com.revature.Flumblr.entities.Theme;
import com.revature.Flumblr.entities.User;
import java.util.List;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {

    Profile getProfileByUser(User user);

    @Modifying
    @Transactional
    @Query("UPDATE Profile p SET p.profile_img = :profileImg WHERE p.id = :id")
    void setProfileImg(String id, String profileImg);

    @Modifying
    @Transactional
    @Query("UPDATE Profile p SET p.bio = :bio WHERE p.id = :id")
    void setBio(String id, String bio);

    @Modifying
    @Transactional
    @Query("UPDATE Profile p SET p.theme = :theme WHERE p.id = :id")
    void setTheme(String id, Theme theme);

    @Query(value = "SELECT p.bio, p.profile_img, u.username FROM profile p JOIN users u ON p.user_id = u.id JOIN profile_tag_list ptl ON ptl.profile_id = p.id JOIN tags t ON t.name = ptl.tag_id WHERE t.name = :tag", nativeQuery = true)
    List<PotentialFollowerResponse> getPotentialListOfFollowers(@Param("tag") String tag);
}