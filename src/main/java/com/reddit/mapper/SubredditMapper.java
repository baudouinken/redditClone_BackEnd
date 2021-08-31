package com.reddit.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.reddit.dto.SubredditDto;
import com.reddit.model.Post;
import com.reddit.model.Subreddit;


@Mapper(componentModel = "spring")
public interface SubredditMapper {

  @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
  SubredditDto mapSubredditToDto(Subreddit subreddit);

  default int mapPosts(List<Post> numberOfPosts) {
    return numberOfPosts.size();
  }

  @InheritInverseConfiguration
  @Mapping(target = "posts", ignore = true)
  Subreddit mapDtoToSubreddit(SubredditDto subredditDto);

}
