package com.reddit.dto;

import java.util.UUID;

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

}
