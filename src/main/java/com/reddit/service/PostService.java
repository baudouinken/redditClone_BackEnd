package com.reddit.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.reddit.dto.PostDto;
import com.reddit.dto.PostResponse;
import com.reddit.exception.SpringRedditException;
import com.reddit.model.Post;
import com.reddit.model.Subreddit;
import com.reddit.model.User;
import com.reddit.repository.CommentRepository;
import com.reddit.repository.PostRepository;
import com.reddit.repository.SubredditRepository;
import com.reddit.repository.UserRepository;

@Service
public class PostService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AuthService authService;

  @Autowired
  private SubredditRepository subredditRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private CommentRepository commentRepository;

  public Post save(PostDto postDto) {

    User user = authService.getCurrentUser();

    Subreddit subreddit = subredditRepository.findById(postDto.getSubredditId())
        .orElseThrow(() -> new SpringRedditException("Cannot find subreddit with id " + postDto.getSubredditId()));

    Post post = Post.builder()
        .postName(postDto.getPostName())
        .url(postDto.getUrl())
        .description(postDto.getDescription())
        .subreddit(subreddit)
        .user(user)
        .createdDate(Instant.now())
        .voteCount(0)
        .build();

    return postRepository.save(post);
  }

  public List<PostResponse> getAllPosts() {
    return postRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
  }

  public PostResponse getPostById(UUID id) {
    // TODO Auto-generated method stub
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("No Post found with the id " + id));

    return PostResponse.builder()
        .id(post.getId())
        .postName(post.getPostName())
        .url(post.getUrl())
        .description(post.getDescription())
        .userName(post.getUser().getUsername())
        .subredditName(post.getSubreddit().getName())
        .voteCount(post.getVoteCount())
        .commentCount(commentCount(post))
        .duration(getDuration(post))
        .build();
  }

  public PostDto update(UUID id, PostDto postDto) {
    // TODO Auto-generated method stub

    User user = userRepository.findById(postDto.getUserId())
        .orElseThrow(() -> new SpringRedditException("Cannot find user with id " + postDto.getUserId()));

    Subreddit subreddit = subredditRepository.findById(postDto.getSubredditId())
        .orElseThrow(() -> new SpringRedditException("Cannot find subreddit with id " + postDto.getSubredditId()));

    Post post = postRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("No Post found with the id " + id));

    post.setPostName(postDto.getPostName());
    post.setUrl(postDto.getUrl());
    post.setDescription(postDto.getDescription());
    post.setUser(user);
    post.setSubreddit(subreddit);

    post = postRepository.save(post);

    return PostDto.builder()
        .postId(post.getId())
        .subredditId(post.getSubreddit().getId())
        .postName(post.getPostName())
        .description(post.getDescription())
        .url(post.getUrl())
        .userId(post.getUser().getId())
        .build();
  }

  public void deletePost(UUID id) {
    // TODO Auto-generated method stub
    Post s = postRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("No Post found with the id " + id));
    postRepository.deleteById(s.getId());
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
        .build();

    return response;
  }

  public List<PostResponse> getPostByUsername(String name) {
    // TODO Auto-generated method stub
    User user = userRepository.findByUsername(name)
        .orElseThrow(() -> new SpringRedditException("Cannot find user with id " + name));

    return postRepository.findByUser(user).stream().map(this::mapToDto).collect(Collectors.toList());
  }

  Integer commentCount(Post post) {
    return commentRepository.findByPost(post).size();
  }

  String getDuration(Post post) {
    return TimeAgo.using(post.getCreatedDate().toEpochMilli());
  }

}
