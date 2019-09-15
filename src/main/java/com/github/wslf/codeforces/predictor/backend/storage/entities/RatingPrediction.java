package com.github.wslf.codeforces.predictor.backend.storage.entities;

import com.github.wslf.codeforces.rating.calculator.entities.ContestantResult;

import java.util.List;

public class RatingPrediction {
  long predictionTime;
  List<ContestantResult> prediction;
}
