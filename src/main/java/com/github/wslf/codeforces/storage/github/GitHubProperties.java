package com.github.wslf.codeforces.storage.github;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.github")
@Data
public class GitHubProperties {
  private String userName;
  private String userToken;
}
