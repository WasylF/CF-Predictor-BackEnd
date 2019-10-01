package com.github.wslf.codeforces.predictor.backend.storage.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingList {
  private ArrayList<UserRating> ratings;
}
