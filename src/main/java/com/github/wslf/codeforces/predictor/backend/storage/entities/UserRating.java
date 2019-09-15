package com.github.wslf.codeforces.predictor.backend.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRating {
  String handle;
  int rating;
}