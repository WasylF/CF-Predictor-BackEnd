package com.github.wslf.codeforces.predictor.backend.storage.github.properties;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.github.current.rating")
@AllArgsConstructor
@NoArgsConstructor
public class CurrentRatingProperties extends StrategyProperties {
}
