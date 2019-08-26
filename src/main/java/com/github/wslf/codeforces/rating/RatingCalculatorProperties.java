package com.github.wslf.codeforces.rating;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rating.calculator")
@Data
public class RatingCalculatorProperties {

  private int threadNumber;
  private int timeToRefreshCacheSeconds;

}