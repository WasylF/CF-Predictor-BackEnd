package com.wslfinc.cf.sdk.rating;

import static com.wslfinc.cf.sdk.Constants.FAKE_DELTAS;
import static com.wslfinc.cf.sdk.Constants.INITIAL_RATING;

import com.wslfinc.cf.sdk.CodeForcesSDK;
import com.wslfinc.cf.sdk.entities.additional.Contestant;
import com.wslfinc.cf.sdk.entities.additional.ContestantResult;
import com.wslfinc.cf.sdk.entities.additional.Team;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Wsl_F
 */
public class RatingCalculatorTeam {

  int numberOfContestants;
  ArrayList<Team> allContestants;
  private int minDelta = 500;
  private int maxDelta = 1200;

  public RatingCalculatorTeam(List<Team> allContestants) {
    this.allContestants = (ArrayList<Team>) allContestants;
    this.numberOfContestants = allContestants.size();
    recalculateRanks();
    Collections.sort(this.allContestants);
    Collections.reverse(this.allContestants);
  }

  public static RatingCalculatorTeam getRatingCalculator(int contestId) {
    List<Team> active = CodeForcesSDK.getActiveTeams(contestId);
    return new RatingCalculatorTeam(active);
  }

  private static double getEloWinProbability(double ra, double rb) {
    return 1.0 / (1 + Math.pow(10, (rb - ra) / 400.0));
  }

  public void setMinDelta(int minDelta) {
    this.minDelta = minDelta;
  }

  public void setMaxDelta(int maxDelta) {
    this.maxDelta = maxDelta;
  }

  private void recalculateRanks() {
    Comparator<Team> rankComp = new Comparator<Team>() {
      @Override
      public int compare(Team contestant1, Team contestan2) {
        return Integer.compare(contestant1.getRank(), contestan2.getRank());
      }
    };

    Collections.sort(allContestants, rankComp);

    {
      int i = 0;
      while (i < numberOfContestants) {
        int first = i;
        int last = first;
        int rank = allContestants.get(first).getRank();
        while (last < numberOfContestants
            && allContestants.get(last).getRank() == rank) {
          last++;
        }
        rank = last;
        for (int j = first; j < last; j++) {
          allContestants.get(j).setRank(rank);
        }
        i = last;
      }
    }
  }

  private double getSeed(Team contestant) {
    double seed = 1;
    double rating = contestant.getPrevRating();
    for (Team opponent : allContestants) {
      if (contestant != opponent) {
        seed += getEloWinProbability(opponent.getPrevRating(), rating);
      }
    }

    return seed;
  }

  private double getSeed(double rating, Team contestant) {
    double seed = 1;
    for (Team opponent : allContestants) {
      if (opponent != contestant) {
        seed += getEloWinProbability(opponent.getPrevRating(), rating);
      }
    }

    return seed;
  }

  private double getAverageRank(Team contestant) {
    double realRank = contestant.getRank();
    double expectedRank = getSeed(contestant);
    double average = Math.sqrt(realRank * expectedRank);

    return average;
  }

  private int getRatingForRank(Team contestant) {
    double averageRank = getAverageRank(contestant);

    int left = Math.max(1, contestant.getPrevRating() - 2 * minDelta);
    int right = contestant.getPrevRating() + 2 * maxDelta;

    while (right - left > 1) {
      int mid = (left + right) / 2;
      double seed = getSeed(mid, contestant);
      if (seed < averageRank) {
        right = mid;
      } else {
        left = mid;
      }
    }

    return left;
  }

  private ArrayList<Integer> calculateDeltas() {
    ArrayList<Integer> deltas = new ArrayList<>();

    for (int i = 0; i < numberOfContestants; i++) {
      Team contestant = allContestants.get(i);
      int expR = getRatingForRank(contestant);
      deltas.add((expR - contestant.getPrevRating()) / 2);
    }

    // Total sum should not be more than zero.
    {
      int sum = 0;
      for (int delta : deltas) {
        sum += delta;
      }
      int inc = -sum / numberOfContestants - 1;
      for (int i = 0; i < numberOfContestants; i++) {
        int delta = deltas.get(i) + inc;
        deltas.set(i, delta);
      }
    }

    // Sum of top-4*sqrt should be adjusted to zero.
    {
      int sum = 0;
      int zeroSumCount = Math.min((int) (4 * Math.round(Math.sqrt(numberOfContestants))), numberOfContestants);

      for (int i = 0; i < zeroSumCount; i++) {
        sum += deltas.get(i);
      }
      int inc = Math.min(Math.max(-sum / zeroSumCount, -10), 0);
      for (int i = 0; i < numberOfContestants; i++) {
        int delta = deltas.get(i) + inc;
        deltas.set(i, delta);
      }
    }

    return deltas;
  }

  public List<ContestantResult> getNewRatings() {
    if (numberOfContestants < 2) {
      return new ArrayList<>();
    }

    ArrayList<Integer> deltas = calculateDeltas();

    List<ContestantResult> results = new ArrayList<>(numberOfContestants);

    for (int i = 0; i < numberOfContestants; i++) {
      Team team = allContestants.get(i);
      double seed = getSeed(team);
      for (Contestant contestant : team.getContestants()) {
        int nextRating = contestant.getPrevRating() + deltas.get(i);
        results.add(new ContestantResult(contestant, seed, nextRating));
      }
      // Add a "fake" contestant with team name, so the extension can find rating change looking for team name.
      if (team.getContestants().length > 1) {
        Contestant teamAdd = new Contestant(team.getName(), team.getRank(),
            new RatingAndContestCount(0, FAKE_DELTAS.length + 1));
        results.add(new ContestantResult(teamAdd, seed, deltas.get(i)));
      }
    }

    return results;
  }

  public List<ContestantResult> getNewRatingsWithFakeDeltas() {
    var results = getNewRatings();
    for (int i = 0; i < numberOfContestants; i++) {
      Team team = allContestants.get(i);
      for (Contestant contestant : team.getContestants()) {
        int contestCount = contestant.getContestCount();
        if (contestCount < FAKE_DELTAS.length) {
          int prevRating = FakeRatingConverter.getFakeRating(contestant.getPrevRating(), contestCount);
          int nextRating = FakeRatingConverter.getFakeRating(results.get(i).getNextRating(), contestCount+1);
          results.get(i).getContestant().setPrevRating(prevRating);
          results.get(i).setNextRating(nextRating);
        }
      }
    }

    return results;
  }
}
