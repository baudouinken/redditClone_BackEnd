package com.reddit.dto;

import java.util.UUID;

import com.reddit.model.VoteType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteDto {

  private VoteType voteType;
  private UUID postId;

}
