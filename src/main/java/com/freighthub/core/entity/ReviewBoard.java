package com.freighthub.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review_board")
@PrimaryKeyJoinColumn(name = "uid")
public class ReviewBoard extends User {

  @Column(name = "user_name", nullable = false, unique = true)
  private String businessName;

}