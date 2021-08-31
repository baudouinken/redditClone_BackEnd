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

import com.reddit.dto.SubredditDto;
import com.reddit.model.Subreddit;
import com.reddit.service.SubredditService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/subreddit")
@AllArgsConstructor
@Slf4j
public class SubRedditController {

  @Autowired
  private SubredditService subredditService;

  @PostMapping
  public Subreddit createSubreddit(@RequestBody SubredditDto subredditDto) {
    return subredditService.save(subredditDto);
  }

  @GetMapping
  public List<SubredditDto> getAll() {
    return subredditService.getAll();
  }

  @GetMapping("/{id}")
  public SubredditDto getSubreddit(@PathVariable UUID id) {
    return subredditService.getSubredditById(id);
  }

  @PutMapping("/{id}")
  public SubredditDto updateSubreddit(@PathVariable UUID id, @RequestBody SubredditDto subredditDto) {
    return subredditService.update(id, subredditDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable UUID id) {
    subredditService.delete(id);
    return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted");
  }

}
