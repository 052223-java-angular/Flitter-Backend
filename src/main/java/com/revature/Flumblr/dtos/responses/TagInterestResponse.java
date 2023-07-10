package com.revature.Flumblr.dtos.responses;

import java.util.Set;
import com.revature.Flumblr.entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TagInterestResponse {

    private Set<Tag> tags;

}
