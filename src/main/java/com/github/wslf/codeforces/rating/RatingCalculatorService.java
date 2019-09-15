package com.github.wslf.codeforces.rating;

import com.github.wslf.codeforces.rating.calculator.entities.ContestantResult;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@EnableConfigurationProperties(RatingCalculatorProperties.class)
public class RatingCalculatorService {

  private final ExecutorService executor;
  private LoadingCache<Integer, List<ContestantResult>> cache;

  @Autowired
  public RatingCalculatorService(RatingCalculatorProperties properties) {
    this.executor = Executors.newFixedThreadPool(properties.getThreadNumber());
    initCache(properties.getTimeToRefreshCacheSeconds());
  }

  private void initCache(int timeToRefreshSeconds) {
    PredictionLoader predictionLoader = new PredictionLoader(executor);
    cache = CacheBuilder.newBuilder().refreshAfterWrite(timeToRefreshSeconds, TimeUnit.SECONDS).build(predictionLoader);
  }

  public List<ContestantResult> getNewRating(int contestId) throws ExecutionException {
    return cache.get(contestId);
  }

  public Set<Integer> getCachedKeys() {
    return cache.asMap().keySet();
  }

  public void remove(int contestId) {
    cache.invalidate(contestId);
  }

}
