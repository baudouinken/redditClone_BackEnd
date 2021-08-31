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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Subreddit {
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(nullable = false, updatable = false)
  private UUID id;

  @NotBlank(message = "Community name is required")
  private String name;

  @NotBlank(message = "Description is required")
  private String description;

  private Instant createdDate;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;
  
  @OneToMany(mappedBy = "subreddit")
  @JsonIgnore
  private List<Post> posts = new ArrayList<>();
}
