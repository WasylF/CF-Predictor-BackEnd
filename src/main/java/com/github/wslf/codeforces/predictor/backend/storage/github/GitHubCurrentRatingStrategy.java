package com.github.wslf.codeforces.predictor.backend.storage.github;

import com.github.wslf.codeforces.predictor.backend.storage.CurrentRatingStrategy;
import com.github.wslf.codeforces.predictor.backend.storage.StorageException;
import com.github.wslf.codeforces.predictor.backend.storage.entities.RatingList;
import com.github.wslf.codeforces.predictor.backend.storage.github.properties.CurrentRatingProperties;
import com.github.wslf.codeforces.predictor.backend.storage.github.properties.GitHubProperties;
import org.springframework.beans.factory.annotation.Autowired;

public class GitHubCurrentRatingStrategy implements CurrentRatingStrategy {

  private final GitHubStorageStrategy<RatingList> storageStrategy;
  private final Identifier identifier;

  @Autowired
  public GitHubCurrentRatingStrategy(GitHubProperties gitHubProperties,
                                     CurrentRatingProperties currentRatingProperties) {
    this.storageStrategy = new GitHubStorageStrategy<>(gitHubProperties);
    String path = new PathBuilder().currentRatingPath();
    this.identifier = new IdentifierBuilder().build(currentRatingProperties, path, RatingList.class);
  }

  @Override
  public RatingList load() throws StorageException {
    return storageStrategy.load(identifier, null);
  }

  @Override
  public void save(RatingList ratingList, int contestId) throws StorageException {
    OptionalParameters parameters = new OptionalParameters();
    parameters.setCommitMessage("Rating after contest " + contestId);
    storageStrategy.save(ratingList, identifier, parameters);
  }

}
