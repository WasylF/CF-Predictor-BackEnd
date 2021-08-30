package com.wslfinc.cf.sdk.rating;

import static com.wslfinc.cf.sdk.Constants.INITIAL_RATING;

public class RatingAndContestCount {
    public int rating;
    public int contest_count;

    public RatingAndContestCount(int rating, int contest_count) {
        this.rating = rating;
        this.contest_count = contest_count;
    }
}
