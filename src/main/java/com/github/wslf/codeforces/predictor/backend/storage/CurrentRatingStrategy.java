package com.github.wslf.codeforces.predictor.backend.storage;

import com.github.wslf.codeforces.predictor.backend.storage.entities.RatingList;

public interface CurrentRatingStrategy {
  RatingList load() throws StorageException;

  void save(RatingList ratingList, int contestId) throws StorageException;
}
