package com.reddit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reddit.dto.PostDto;
import com.reddit.dto.PostResponse;
import com.reddit.model.Post;
import com.reddit.service.PostService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
@Slf4j
public class PostController {

  @Autowired
  private PostService postService;

  @PostMapping
  public Post createPost(@RequestBody PostDto postDto) {
    return postService.save(postDto);
  }

  @GetMapping
  public List<PostResponse> getAllPosts() {
    return postService.getAllPosts();
  }

  @GetMapping("/{id}")
  public PostResponse getPost(@PathVariable UUID id) {
    return postService.getPostById(id);
  }

  @GetMapping("/user/{name}")
  public List<PostResponse> getPostByUsername(@PathVariable String name) {
    return postService.getPostByUsername(name);
  }

  @PutMapping("/{id}")
  public PostDto updatePost(@PathVariable UUID id, @RequestBody PostDto postDto) {
    return postService.update(id, postDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePost(@PathVariable UUID id) {
    postService.deletePost(id);
    return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
  }

}
