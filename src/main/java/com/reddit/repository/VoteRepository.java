package com.reddit.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.reddit.model.Post;
import com.reddit.model.User;
import com.reddit.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, UUID>, JpaSpecificationExecutor<Vote> {

  Optional<Vote> findTopByPostAndUserOrderByIdDesc(Post post, User currentUser);
  
  Optional<Vote> findByPostAndUser(Post post, User currentUser);

}
