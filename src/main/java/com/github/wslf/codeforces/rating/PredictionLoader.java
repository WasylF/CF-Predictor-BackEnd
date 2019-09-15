package com.github.wslf.codeforces.rating;

import com.github.wslf.codeforces.rating.calculator.entities.ContestantResult;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class PredictionLoader extends CacheLoader<Integer, List<ContestantResult>> {
  private final ExecutorService executor;

  public PredictionLoader(ExecutorService executor) {
    this.executor = executor;
  }

  @Override
  public List<ContestantResult> load(Integer contestId) {
    return Collections.EMPTY_LIST;
  }

  @Override
  public ListenableFuture<List<ContestantResult>> reload(Integer contestId, List<ContestantResult> oldValue) {
    ListenableFutureTask<List<ContestantResult>> task = ListenableFutureTask.create(() -> {
      return null;
    });
    executor.execute(task);
    return task;
  }
}
