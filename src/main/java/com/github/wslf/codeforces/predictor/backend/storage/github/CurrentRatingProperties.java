package com.github.wslf.codeforces.predictor.backend.storage.github;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.github.current.rating")
public class CurrentRatingProperties extends StrategyProperties {
}
