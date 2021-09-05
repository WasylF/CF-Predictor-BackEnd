package com.wslfinc.cf.sdk;

import static com.wslfinc.cf.sdk.CodeForcesSDK.getRanklistRows;
import static com.wslfinc.cf.sdk.Constants.INITIAL_RATING;
import static com.wslfinc.cf.sdk.Constants.MAX_RATING_EDUCATIONAL_PARTICIPANT;

import com.wslfinc.cf.sdk.entities.Member;
import com.wslfinc.cf.sdk.entities.RanklistRow;
import com.wslfinc.cf.sdk.entities.additional.Contestant;
import com.wslfinc.cf.sdk.entities.additional.Team;
import com.wslfinc.cf.sdk.rating.RatingAndContestCount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wsl_F
 */
public class ContestantProcessing {

  private static ContestantsCached allContestants = new ContestantsCached();

  /**
   * IT'S NOT OFFICIAL CODEFORCES API!!!
   * <p>
   * Getting rating of all contestants just before round № {@code contestId}
   *
   * @param contestId Id of the contest. It is not the round number. It can be seen in contest URL. {@code 1 <=
   *                  contestId <= MAXIMAL_CONTEST_ID}
   * @return List of Contestant. Rank field of every contestant contains default value (-1).
   */
  static List<Contestant> getAllContestants(int contestId) {
    return allContestants.getValue(contestId);
  }

  /**
   * IT'S NOT OFFICIAL CODEFORCES API!!!
   * <p>
   * Getting rating of all contestants just before round № {@code contestId}
   *
   * @param contestId Id of the contest. It is not the round number. It can be seen in contest URL. {@code 1 <=
   *                  contestId <= MAXIMAL_CONTEST_ID}
   * @return List of Contestants that particapating in contest #{@code contestId}. All fields of Contestant are filled.
   */
  static ArrayList<Team> getActiveTeams(int contestId) {
    List<Contestant> registeredContestants = getAllContestants(contestId);
    List<RanklistRow> rows = getRanklistRows(contestId);

    Map<String, RatingAndContestCount> prevRating = new HashMap<>();
    for (Contestant contestant : registeredContestants) {
      prevRating.put(contestant.getHandle(),
          new RatingAndContestCount(contestant.getPrevRating(), contestant.getContestCount()));
    }

    return getActiveTeams(contestId, prevRating, rows);
  }

  static ArrayList<Team> getActiveTeams(int contestId, Map<String, RatingAndContestCount> prevRating,
      List<RanklistRow> rows) {
    ArrayList<Team> active = new ArrayList<>();
    for (RanklistRow row : rows) {
      ArrayList<Contestant> teammates = new ArrayList<>();
      for (Member member : row.getParty().getMembers()) {
        String handle = member.getHandle();
        if (!prevRating.containsKey(handle)) {
          prevRating.put(handle, new RatingAndContestCount(INITIAL_RATING, 0));
        }

        RatingAndContestCount prevR = prevRating.get(handle);
        int rank = row.getRank();
        if (ContestProcessing.isEducational(contestId) && prevR.rating >= MAX_RATING_EDUCATIONAL_PARTICIPANT) {
          teammates.clear();
          break;
        }
        teammates.add(new Contestant(handle, rank, prevR));
      }

      if (teammates.size() > 0) {
        String name = teammates.size() == 1 ? teammates.get(0).getHandle()
            : row.getParty().getTeamName();
        active.add(new Team(teammates, name));
      }
    }

    return active;
  }

}
