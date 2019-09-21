package com.github.wslf.codeforces.predictor.backend;

import com.github.wslf.codeforces.predictor.backend.rating.RatingCalculatorProperties;
import com.github.wslf.codeforces.predictor.backend.storage.github.CurrentRatingProperties;
import com.github.wslf.codeforces.predictor.backend.storage.github.GitHubProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties({RatingCalculatorProperties.class, GitHubProperties.class, CurrentRatingProperties.class})
public class PredictorBackEnd {
  private static final List<String> PROPERTIES_FILES = List.of(
          "application",
          "storage.github",
          "storage.github.current.rating.properties"
  );

  public static void main(String[] args) {
    new SpringApplicationBuilder(PredictorBackEnd.class)
        .properties("spring.config.name:"+String.join(",", PROPERTIES_FILES))
        .build()
        .run(args);
  }
}
