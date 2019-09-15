package com.github.wslf.codeforces.predictor.backend;

import com.github.wslf.codeforces.predictor.backend.rating.RatingCalculatorProperties;
import com.github.wslf.codeforces.predictor.backend.storage.github.GitHubProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({RatingCalculatorProperties.class, GitHubProperties.class})
public class PredictorBackEnd {
  public static void main(String[] args) {
    new SpringApplicationBuilder(PredictorBackEnd.class)
        .properties("spring.config.name:application,storage.github")
        .build()
        .run(args);
  }
}
