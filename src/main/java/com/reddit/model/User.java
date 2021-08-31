package com.reddit.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="User_") 
public class User {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(nullable = false, updatable = false)
  private UUID id;

  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "Password is required")
  private String password;

  @Email
  @NotEmpty(message = "Email is required")
  private String email;

  private Instant created;

  private boolean enabled;

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Comment> comment = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Post> post = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Subreddit> subreddits = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Vote> votes = new ArrayList<>();
}