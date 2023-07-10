package com.revature.Flumblr.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.revature.Flumblr.entities.Tag;
import com.revature.Flumblr.repositories.TagRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public void saveTag(Tag tag) {
        tagRepository.save(tag);
    }

    public List<Tag> getTags(String tagId) {
        if(tagId != null) {
            return tagRepository.findAllById(tagId);
        } else {
            return tagRepository.findAll();
        }
    }

    public void deleteTag(String tagId) {
        tagRepository.deleteById(tagId);
    }

    public Tag findByName(String name) {
        Optional<Tag> tag = tagRepository.findByName(name);
        if (tag.isEmpty()) {
            return createTag(name);
        }
        return tag.get();
    }

    public Tag createTag(String name) {

        Tag tag = new Tag(name);
        tagRepository.save(tag);
        return tag;
    }

}
