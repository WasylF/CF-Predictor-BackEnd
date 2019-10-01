package com.github.wslf.codeforces.predictor.backend.storage.entities;

import com.github.wslf.codeforces.rating.calculator.entities.ContestantResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingPrediction {
  private long predictionTime;
  private List<ContestantResult> prediction;
}
