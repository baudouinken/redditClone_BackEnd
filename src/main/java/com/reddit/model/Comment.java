package com.reddit.model;

import static javax.persistence.FetchType.LAZY;

import java.time.Instant;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(nullable = false, updatable = false)
  private UUID id;

  @NotEmpty
  private String text;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  private Instant createdDate;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

}
