package com.wslfinc.cf;

/**
 * @author Wsl_F
 */
public class RatingDiff implements Comparable<RatingDiff> {

  int contestId;
  String handle;
  int predictedRating;
  int realRating;
  int diff;
  int prevRating;
  int myPrevRating;
  int rank;
  double seed;
  int contestCount;

  public RatingDiff(int contestId, String handle, int predictedRating, int realRating, int prevRating, int myPrevRating,
      int rank,
      double seed, int contestCount) {
    this.contestId = contestId;
    this.handle = handle;
    this.predictedRating = predictedRating;
    this.realRating = realRating;
    this.prevRating = prevRating;
    this.myPrevRating = myPrevRating;
    this.rank = rank;
    this.seed = seed;
    this.contestCount = contestCount;
    this.diff = Math.abs(predictedRating - realRating);
  }

  public static String describeFields() {
    return "diff\t\treal\tmy\t\trank\t\tprev\tmy_prev\t\tcnt\tcontestId\thandle\tseed";
  }

  @Override
  public int compareTo(RatingDiff rd) {
    if (this.diff == rd.diff) {
      return this.handle.compareTo(rd.handle);
    }

    return this.diff < rd.diff ? -1 : 1;
  }

  public String serialize() {
    return String.format("%4d\t\t%4d\t%4d\t\t%5d\t\t%4d\t%4d\t\t%5d\t%4d\t%s\t%.1f", diff, realRating, predictedRating,
        rank, prevRating, myPrevRating, contestCount, contestId, handle, seed);
  }
}
