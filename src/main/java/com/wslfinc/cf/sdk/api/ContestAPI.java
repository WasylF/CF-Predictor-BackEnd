package com.wslfinc.cf.sdk.api;

import com.wslfinc.cf.sdk.ResponseChecker;
import com.wslfinc.cf.sdk.entities.Contest;
import com.wslfinc.cf.sdk.entities.RanklistRow;
import com.wslfinc.cf.sdk.entities.RatingChange;
import com.wslfinc.helpers.web.JsonReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.wslfinc.cf.sdk.Constants.API_PREFIX;
import static com.wslfinc.cf.sdk.Constants.JSON_RESULTS;

/**
 * @author Wsl_F
 */
class ContestAPI {

  /**
   * @param contestId codeforces contest id (!!! not equals cf round number)
   * @return rating changes
   */
  static List<RatingChange> getRatingChanges(int contestId) {
    try {
      String url = API_PREFIX + "/contest.ratingChanges?contestId=" + contestId;
      JSONObject response = JsonReader.read(url);
      if (ResponseChecker.isValid(response)) {
        JSONArray ratingChanges = response.getJSONArray(JSON_RESULTS);

        List<RatingChange> changes = new ArrayList<>();
        for (Object ratingChange : ratingChanges) {
          changes.add(new RatingChange((JSONObject) ratingChange));
        }
        return changes;
      }
    } catch (Exception exception) {
      System.err.println("Failed to get rating changens on contestId: " + contestId);
      System.err.println(exception.getMessage());
    }

    return new ArrayList<>();
  }

  /**
   * @param gym include gym contests
   * @return rating changes
   */
  static List<Contest> getContestsList(boolean gym) {
    try {
      String url = API_PREFIX + "/contest.list?gym=" + gym;
      JSONObject response = JsonReader.read(url);
      if (ResponseChecker.isValid(response)) {
        JSONArray array = response.getJSONArray(JSON_RESULTS);

        List<Contest> results = new ArrayList<>();
        for (Object item : array) {
          results.add(new Contest((JSONObject) item));
        }
        return results;
      }
    } catch (Exception exception) {
      System.err.println("Failed to get contests list");
      System.err.println(exception.getMessage());
    }

    return new ArrayList<>();
  }

  /**
   * @param contestId      Id of the contest. It is not the round number. It can be
   *                       seen in contest URL.
   * @param from           1-based index of the standings row to start the ranklist.
   * @param count          Number of standing rows to return.
   * @param showUnofficial if true than all participants (virtual, out of
   *                       competition) are shown. Otherwise, only official contestants are shown.
   * @param contest        returning result
   * @param problems       returning result
   * @param rows           returning result
   * @return successfulness
   */
  static boolean getContestStanding(int contestId, int from, int count, boolean showUnofficial,
                                    Contest contest, Object problems, List<RanklistRow> rows) {
    String url = API_PREFIX + "/contest.standings"
      + "?contestId=" + contestId
      + "&from=" + from
      + "&count=" + count
      + "&showUnofficial=" + showUnofficial;

    try {
      JSONObject response = JsonReader.read(url);
      if (ResponseChecker.isValid(response)) {
        JSONObject result = response.getJSONObject(JSON_RESULTS);

        contest.setAll(result.getJSONObject("contest"));
        problems = result.getJSONArray("problems");
        JSONArray array = result.getJSONArray("rows");

        for (Object item : array) {
          rows.add(new RanklistRow((JSONObject) item));
        }
        return true;
      }

    } catch (Exception ex) {
      System.err.println("Failed to get contests standings. Url: " + url
        + "\n" + ex.getMessage());
    }
    return false;
  }

}
