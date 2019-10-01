package com.github.wslf.codeforces.predictor.backend.storage.github.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.github")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitHubProperties {
  private String userName;
  private String userToken;
}
