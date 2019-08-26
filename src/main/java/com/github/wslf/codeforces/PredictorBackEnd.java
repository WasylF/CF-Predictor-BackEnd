package com.github.wslf.codeforces;

import com.github.wslf.codeforces.rating.RatingCalculatorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RatingCalculatorProperties.class)
public class PredictorBackEnd {
  public static void main(String[] args) {
    SpringApplication.run(PredictorBackEnd.class, args);
  }
}
