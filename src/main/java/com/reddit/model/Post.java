package com.reddit.model;

import static javax.persistence.FetchType.LAZY;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(nullable = false, updatable = false)
  private UUID id;

  @NotBlank(message = "Post Name cannot be empty or Null")
  private String postName;

  @Nullable
  private String url;

  @Nullable
  private String description;

  private Integer voteCount = 0;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private Instant createdDate;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "subreddit_id")
  private Subreddit subreddit;
  
  @OneToMany(mappedBy = "post")
  @JsonIgnore
  private List<Comment> comment = new ArrayList<>();
  
  @OneToMany(mappedBy = "post")
  @JsonIgnore
  private List<Vote> votes = new ArrayList<>();
}
