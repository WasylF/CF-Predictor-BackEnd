package com.github.wslf.codeforces.predictor.backend.storage.github.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyProperties {
  private String branch;
  private String directory;
  private String repository;
  private String commitMessagePattern;
}
