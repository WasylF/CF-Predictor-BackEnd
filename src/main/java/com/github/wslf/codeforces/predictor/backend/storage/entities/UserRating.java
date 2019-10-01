package com.github.wslf.codeforces.predictor.backend.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRating {
  private String handle;
  private int rating;
}
