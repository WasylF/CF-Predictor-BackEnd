package com.github.wslf.codeforces.predictor.backend.storage.github;

import lombok.Data;

@Data
public class StrategyProperties {
  private String branch;
  private String directory;
  private String repository;
  private String commitMessagePattern;
}
