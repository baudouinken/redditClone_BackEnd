package com.reddit.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  
  private static final Logger logger = LoggerFactory.getLogger(VoteService.class);

  @Transactional
  public void vote(VoteDto voteDto) {
    Post post = postRepository.findById(voteDto.getPostId())
        .orElseThrow(() -> new SpringRedditException("Post Not Found with ID - " + voteDto.getPostId()));
    Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByIdDesc(post,
        authService.getCurrentUser());
    
    logger.info("fund optional");
    
    if (voteByPostAndUser.isPresent() &&
        voteByPostAndUser.get().getVoteType()
            .equals(voteDto.getVoteType())) {
      throw new SpringRedditException("You have already " + voteDto.getVoteType() + "'d for this post");
    } else {
      if (VoteType.UPVOTE.equals(voteDto.getVoteType())) {
        post.setVoteCount(post.getVoteCount() + 1);
        logger.info("upvoted");
      } else if(VoteType.DOWNVOTE.equals(voteDto.getVoteType())){
        logger.info("downvoted");
        post.setVoteCount(post.getVoteCount() - 1);
      }
    }
    
    voteRepository.save(mapToVote(voteDto, post));
    postRepository.save(post);
    logger.info("saved");
  }

  private Vote mapToVote(VoteDto dto, Post post) {
    return Vote.builder().voteType(dto.getVoteType()).post(post).user(authService.getCurrentUser()).build();
  }

}
