package com.github.wslf.codeforces.predictor.backend.storage.entities;

import com.github.wslf.codeforces.rating.calculator.entities.ContestantResult;
import lombok.Data;

import java.util.List;

@Data
public class RatingPrediction {
  private long predictionTime;
  private List<ContestantResult> prediction;
}
