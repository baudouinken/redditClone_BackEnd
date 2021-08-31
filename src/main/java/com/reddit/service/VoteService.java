package com.reddit.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reddit.dto.VoteDto;
import com.reddit.exception.SpringRedditException;
import com.reddit.model.Post;
import com.reddit.model.Vote;
import com.reddit.model.VoteType;
import com.reddit.repository.PostRepository;
import com.reddit.repository.VoteRepository;

@Service
public class VoteService {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private VoteRepository voteRepository;

  @Autowired
  private AuthService authService;

  public void vote(VoteDto voteDto) {
    // TODO Auto-generated method stub
    Post post = postRepository.findById(voteDto.getPostId())
        .orElseThrow(() -> new SpringRedditException("Post Not Found"));

    Optional<Vote> voteByPostAndUser = voteRepository.findByPostAndUser(post,
        authService.getCurrentUser());

    if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
      throw new SpringRedditException("You have already " + voteDto.getVoteType() + "'D for this post");
    }

    if (VoteType.UPVOTE.equals(voteDto.getVoteType())) {
      post.setVoteCount((post.getVoteCount() == null ? 0 : post.getVoteCount()) + 1);
    } else {
      post.setVoteCount((post.getVoteCount() == null ? 0 : post.getVoteCount()) - 1);
    }

    voteRepository.save(mapToVote(voteDto, post));
    postRepository.save(post);
  }

  private Vote mapToVote(VoteDto dto, Post post) {
    return Vote.builder().voteType(dto.getVoteType()).post(post).user(authService.getCurrentUser()).build();
  }

}
