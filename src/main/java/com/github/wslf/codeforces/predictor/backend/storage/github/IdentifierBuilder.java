package com.github.wslf.codeforces.predictor.backend.storage.github;

import com.github.wslf.codeforces.predictor.backend.storage.github.properties.StrategyProperties;

import java.lang.reflect.Type;

public class IdentifierBuilder {
  public Identifier build(StrategyProperties strategyProperties, String path, Type type) {
    return new Identifier(strategyProperties.getRepository(), strategyProperties.getBranch(), path, type);
  }
}
