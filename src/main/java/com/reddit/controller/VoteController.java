package com.reddit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reddit.dto.VoteDto;
import com.reddit.service.VoteService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
@Slf4j
public class VoteController {

  @Autowired
  private VoteService voteService;

  @PostMapping
  public ResponseEntity<Void> vote(@RequestBody VoteDto voteDto) {
    voteService.vote(voteDto);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
