package com.reddit.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {

  private UUID postId;
  private UUID subredditId;
  private String postName;
  private String url;
  private String description;
  private UUID userId;
  private Instant createdDate;
  private Integer voteCount = 0;

}
