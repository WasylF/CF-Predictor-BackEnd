package com.github.wslf.codeforces.predictor.backend.storage.github.properties;

import lombok.Data;

@Data
public class StrategyProperties {
  private String branch;
  private String directory;
  private String repository;
  private String commitMessagePattern;
}
