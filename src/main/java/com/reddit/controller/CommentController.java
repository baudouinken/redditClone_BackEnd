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

import com.reddit.dto.CommentDto;
import com.reddit.service.CommentService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
@Slf4j
public class CommentController {
  
 @Autowired
 private CommentService commentService;
 
 @PostMapping
 public CommentDto createComment(@RequestBody CommentDto commentDto) {
   return commentService.save(commentDto);
 }

 @GetMapping
 public List<CommentDto> getAllComments() {
   return commentService.getAllComments();
 }

 @GetMapping("/{id}")
 public CommentDto getComment(@PathVariable UUID id) {
   return commentService.getCommentById(id);
 }
 
 @GetMapping("/user/{name}")
 public List<CommentDto> getCommentByUsername(@PathVariable String name) {
   return commentService.getCommentByUsername(name);
 }
 
 @GetMapping("/post/{postId}")
 public List<CommentDto> getCommentByPost(@PathVariable UUID postId) {
   return commentService.getCommentByPost(postId);
 }

 @PutMapping("/{id}")
 public CommentDto updateComment(@PathVariable UUID id, @RequestBody CommentDto commentDto) {
   return commentService.update(id, commentDto);
 }

 @DeleteMapping("/{id}")
 public ResponseEntity<?> deleteComment(@PathVariable UUID id) {
   commentService.deleteComment(id);
   return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
 }

}
