package com.reddit.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reddit.dto.SubredditDto;
import com.reddit.exception.SpringRedditException;
import com.reddit.mapper.SubredditMapper;
import com.reddit.model.Subreddit;
import com.reddit.repository.SubredditRepository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@NoArgsConstructor
@Slf4j
public class SubredditService {

  private SubredditMapper subredditMapper;

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
        .numberOfPosts(subreddit.getPosts().size()).description(subreddit.getDescription()).build();
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

}
