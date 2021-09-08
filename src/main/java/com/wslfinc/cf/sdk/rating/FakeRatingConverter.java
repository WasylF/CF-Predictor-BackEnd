package com.wslfinc.cf.sdk.rating;

import static com.wslfinc.cf.sdk.Constants.FAKE_DELTAS;
import static com.wslfinc.cf.sdk.Constants.INITIAL_RATING;

public class FakeRatingConverter {

  public static int getFakeRating(int trueRating, int contestCount) {
    if (contestCount <= 0) {
      return 0;
    }
    if (contestCount >= FAKE_DELTAS.length) {
      return trueRating;
    }

    return trueRating - INITIAL_RATING + FAKE_DELTAS[contestCount - 1];
  }
}
