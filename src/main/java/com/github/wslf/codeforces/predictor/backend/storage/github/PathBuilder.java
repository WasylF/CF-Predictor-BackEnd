package com.github.wslf.codeforces.predictor.backend.storage.github;

import org.springframework.stereotype.Component;

@Component
public class PathBuilder {
  public String currentRatingPath() {
    return "contests/current_rating.json";
  }

}
