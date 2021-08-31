package com.reddit.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reddit.dto.CommentDto;
import com.reddit.exception.SpringRedditException;
import com.reddit.model.Comment;
import com.reddit.model.NotificationEmail;
import com.reddit.model.Post;
import com.reddit.model.User;
import com.reddit.repository.CommentRepository;
import com.reddit.repository.PostRepository;
import com.reddit.repository.UserRepository;

@Service
public class CommentService {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private MailContentBuilder mailContentBuilder;
  
  @Autowired
  private MailService mailService;

  @Autowired
  private AuthService authService;
  
  private static final String POST_URL = "";

  public CommentDto save(CommentDto commentDto) {
    // TODO Auto-generated method stub
    User user = authService.getCurrentUser();

    Post post = postRepository.getById(commentDto.getPostId());

    Comment comment = new Comment();
    comment.setCreatedDate(Instant.now());
    comment.setPost(post);
    comment.setUser(user);
    comment.setText(commentDto.getText());

    Comment saved = commentRepository.save(comment);
    
    String message = mailContentBuilder.build(user.getUsername() + " posted a comment on your post." + POST_URL);
    sendCommentNotification(message, post.getUser());
    return CommentDto.builder()
        .id(saved.getId())
        .text(saved.getText())
        .postId(saved.getPost().getId())
        .username(saved.getUser().getUsername())
        .build();
  }
  
  private void sendCommentNotification(String message, User user) {
    mailService.sendMessage(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
}

  public List<CommentDto> getAllComments() {
    // TODO Auto-generated method stub
    return commentRepository.findAll()
        .stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  public CommentDto getCommentById(UUID id) {
    // TODO Auto-generated method stub
    Comment comment = commentRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("cannot find comment with id " + id));
    return CommentDto.builder()
        .id(comment.getId())
        .text(comment.getText())
        .postId(comment.getPost().getId())
        .username(comment.getUser().getUsername())
        .build();
  }

  public List<CommentDto> getCommentByUsername(String name) {
    // TODO Auto-generated method stub
    User user = userRepository.findByUsername(name)
        .orElseThrow(() -> new SpringRedditException("cannot find comment with name " + name));

    List<Comment> comments = commentRepository.findAllByUser(user);

    return comments.stream().map(this::mapToDto).collect(Collectors.toList());
  }

  public CommentDto update(UUID id, CommentDto commentDto) {
    // TODO Auto-generated method stub
    Post post = postRepository.getById(commentDto.getPostId());

    Comment comment = commentRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("cannot find comment with id " + id));

    comment.setText(commentDto.getText());
    comment.setPost(post);

    return CommentDto.builder()
        .id(comment.getId())
        .text(comment.getText())
        .postId(comment.getPost().getId())
        .username(comment.getUser().getUsername())
        .build();
  }

  public void deleteComment(UUID id) {
    // TODO Auto-generated method stub
    Comment comment = commentRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("cannot find comment with id " + id));
    commentRepository.delete(comment);
  }

  private CommentDto mapToDto(Comment comment) {
    return CommentDto.builder()
        .postId(comment.getId())
        .text(comment.getText())
        .username(comment.getUser().getUsername())
        .build();
  }

  public List<CommentDto> getCommentByPost(UUID postId) {
    // TODO Auto-generated method stub
    Post post = postRepository.getById(postId);

    List<Comment> comments = commentRepository.findByPost(post);

    return comments.stream().map(this::mapToDto).collect(Collectors.toList());
  }

}
