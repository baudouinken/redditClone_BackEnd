package com.reddit.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reddit.dto.PostResponse;
import com.reddit.dto.SubredditDto;
import com.reddit.exception.SpringRedditException;
import com.reddit.mapper.SubredditMapper;
import com.reddit.model.Post;
import com.reddit.model.Subreddit;
import com.reddit.model.Vote;
import com.reddit.model.VoteType;
import com.reddit.repository.CommentRepository;
import com.reddit.repository.PostRepository;
import com.reddit.repository.SubredditRepository;
import com.reddit.repository.UserRepository;
import com.reddit.repository.VoteRepository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@Slf4j
public class SubredditService {

  private SubredditMapper subredditMapper;
  
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AuthService authService;

  @Autowired
  private PostRepository postRepository;
  
  @Autowired
  private VoteRepository voteRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private SubredditRepository subredditRepository;

  public Subreddit save(SubredditDto subredditDto) {

    Subreddit subreddit = Subreddit.builder()
        .name("r/"+subredditDto.getName())
        .description(subredditDto.getDescription())
        .build();

    // easy with mapstruct
    //Subreddit subreddit = subredditMapper.mapDtoToSubreddit(subredditDto);
    subreddit.setCreatedDate(Instant.now());

    return subredditRepository.save(subreddit);
  }

  public List<SubredditDto> getAll() {
    return subredditRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
  }

  public SubredditDto getSubredditById(UUID id) {
    // TODO Auto-generated method stub
    Subreddit subreddit = subredditRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("No Subreddit found with the id " + id));

    return SubredditDto.builder().name(subreddit.getName()).id(subreddit.getId())
        .numberOfPosts(subreddit.getPosts().size()).description(subreddit.getDescription()).createdBy(subreddit.getUser().getUsername()).posts(mapToDto(subreddit.getPosts())).build();
  }

  public SubredditDto update(UUID id, SubredditDto subredditDto) {
    // TODO Auto-generated method stub
    Subreddit subreddit = subredditRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("No Subreddit found with the id " + id));

    subreddit.setName(subredditDto.getName());
    subreddit.setDescription(subredditDto.getDescription());

    subredditRepository.save(subreddit);

    return SubredditDto.builder().name(subreddit.getName()).id(subreddit.getId())
        .numberOfPosts(subreddit.getPosts().size()).description(subreddit.getDescription()).build();
  }

  public void delete(UUID id) {
    // TODO Auto-generated method stub
    Subreddit s = subredditRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("No Subreddit found with the id " + id));
    subredditRepository.deleteById(s.getId());
  }

  private SubredditDto mapToDto(Subreddit subreddit) {
    return SubredditDto.builder().name(subreddit.getName()).id(subreddit.getId())
        .numberOfPosts(subreddit.getPosts().size()).description(subreddit.getDescription()).build();
  }
  
  private PostResponse mapToDto(Post post) {

    PostResponse response = PostResponse.builder()
        .id(post.getId())
        .postName(post.getPostName())
        .url(post.getUrl())
        .description(post.getDescription())
        .userName(post.getUser().getUsername())
        .subredditName(post.getSubreddit().getName())
        .voteCount(post.getVoteCount())
        .commentCount(commentCount(post))
        .duration(getDuration(post))
        .upVote(isPostUpVoted(post))
        .downVote(isPostDownVoted(post))
        .build();

    return response;
  }
  
  private List<PostResponse> mapToDto(List<Post> post) {
    return post.stream().map(this::mapToDto).collect(Collectors.toList());
  }
  
  Integer commentCount(Post post) {
    return commentRepository.findByPost(post).size();
  }

  String getDuration(Post post) {
    return TimeAgo.using(post.getCreatedDate().toEpochMilli());
  }
  
  boolean isPostUpVoted(Post post) {
    return checkVoteType(post, VoteType.UPVOTE);
}

boolean isPostDownVoted(Post post) {
    return checkVoteType(post, VoteType.DOWNVOTE);
}

private boolean checkVoteType(Post post, VoteType voteType) {
  if (authService.isLoggedIn()) {
      Optional<Vote> voteForPostByUser =
              voteRepository.findTopByPostAndUserOrderByIdDesc(post,
                      authService.getCurrentUser());
      return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
              .isPresent();
  }
  return false;
}

}
