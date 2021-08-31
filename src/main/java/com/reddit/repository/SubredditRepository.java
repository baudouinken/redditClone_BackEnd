package com.reddit.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.reddit.model.Subreddit;

public interface SubredditRepository extends JpaRepository<Subreddit, UUID>, JpaSpecificationExecutor<Subreddit> {

  Optional<Subreddit> findByName(String subredditName);

}
