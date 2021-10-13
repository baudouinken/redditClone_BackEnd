package com.reddit.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.reddit.model.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubredditDto {

  private UUID id;
  private String name;
  private String description;
  private int numberOfPosts;
  private List<Post> posts = new ArrayList<>();

}
