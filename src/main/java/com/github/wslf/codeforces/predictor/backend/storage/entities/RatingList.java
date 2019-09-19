package com.github.wslf.codeforces.predictor.backend.storage.entities;

import lombok.Data;

import java.util.ArrayList;

@Data
public class RatingList {
  private ArrayList<UserRating> ratings;
}
