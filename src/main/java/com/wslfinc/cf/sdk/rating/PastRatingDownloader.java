package com.wslfinc.cf.sdk.rating;

import static com.wslfinc.cf.sdk.Constants.INITIAL_RATING;
import static com.wslfinc.cf.sdk.Constants.JSON_RESULTS;
import static com.wslfinc.cf.sdk.Constants.OLD_INITIAL_RATING;
import static com.wslfinc.cf.sdk.Constants.SUCCESSFUL_STATUS;

import com.wslfinc.cf.sdk.CodeForcesSDK;
import com.wslfinc.cf.sdk.entities.Contest;
import com.wslfinc.cf.sdk.entities.RatingChange;
import com.wslfinc.cf.sdk.entities.additional.ContestantResult;
import com.wslfinc.helpers.web.JsonReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class is responsible for getting from codeforces all users rating for all previous contests.
 *
 * @author Wsl_F
 */
public class PastRatingDownloader {

  // contest with id 1360 is the first that used "fake deltas" and true starting rating 1400.
  private static boolean fakeDeltasEnabled = true;

  public static boolean getRatingBeforeContest(int maxId, String filePrefix) {
    boolean result = getRatingBeforeContest(-1, maxId, filePrefix);
    return validate(maxId, filePrefix) && result;
  }

  public static TreeMap<String, RatingAndContestCount> loadRatingFromId(int loadContestId, String filePrefix) {
    if (loadContestId > 0) {
      try {
        String path = "file://" + getFileName("" + loadContestId, filePrefix);
        JSONObject json = JsonReader.read(path);
        return toMap(json);
      } catch (Exception ex) {
        System.err.println(ex.toString());
      }
    }

    return new TreeMap<>();
  }

  public static boolean getRatingBeforeContest(int loadFromId, int maxId, String filePrefix) {
    List<Contest> contests = CodeForcesSDK.getFinishedContests(maxId, false);
    TreeMap<String, RatingAndContestCount> rating = loadRatingFromId(loadFromId, filePrefix);
    boolean loaded = false;

    boolean result = true;
    int n = contests.size();
    for (int i = 0; i < n; i++) {
      int contestId = contests.get(i).getId();
      loaded |= contestId == loadFromId;
      if (!loaded) {
        continue;
      }
      System.out.println("Writing rating before contest: " + contestId);
      if (!writeToFiles(filePrefix, rating, "" + contestId)) {
        result = false;
        System.err.println("Couldn't write rating after contest " + contestId);
      }
      addFromCF(contestId, rating);
      // addFromPredictor(contestId, rating);
    }

    result &= writeToFiles(filePrefix, rating, "current");
    return result;
  }

  private static void addFromCF(int contestId, TreeMap<String, RatingAndContestCount> rating) {
    List<RatingChange> ratingChanges = CodeForcesSDK.getRatingChanges(contestId);

//    if (!fakeDeltasEnabled) {
//      for (RatingChange ratingChange : ratingChanges) {
//        if (!rating.containsKey(ratingChange.getHandle()) && ratingChange.getOldRating() == 0) {
//          fakeDeltasEnabled = true;
//          System.err.println("***********************************************");
//          System.err.println("***********************************************");
//          System.err.println("**                                           **");
//          System.err.println("**                                           **");
//          System.err.println("**                  " + contestId + "                     **");
//          System.err.println("**                                           **");
//          System.err.println("**                                           **");
//          System.err.println("***********************************************");
//          System.err.println("***********************************************");
//          break;
//        }
//      }
//    }

    if (contestId == 1360) {
      fakeDeltasEnabled = true;
    }
    for (RatingChange ratingChange : ratingChanges) {
      var handle = ratingChange.getHandle();
      int newRating = ratingChange.getNewRating();
      RatingAndContestCount currentRating;
      if (!fakeDeltasEnabled) {
        currentRating = rating.getOrDefault(handle, new RatingAndContestCount(OLD_INITIAL_RATING, 10_000));
        currentRating.rating = newRating;
      } else {
        currentRating = rating.getOrDefault(handle, new RatingAndContestCount(INITIAL_RATING, 0));
        currentRating.rating = FakeRatingConverter.getTrueRating(newRating, currentRating.contestCount + 1);
      }
      currentRating.contestCount++;
      rating.put(handle, currentRating);
    }
  }

  private static void addFromPredictor(int contestId, TreeMap<String, RatingAndContestCount> rating) {
    List<ContestantResult> ratingChanges = CodeForcesSDK.getNewRatings(contestId);
    for (ContestantResult ratingChange : ratingChanges) {
      var handle = ratingChange.getHandle();
      int newRating = ratingChange.getNextRating();
      RatingAndContestCount currentRating;
      if (!fakeDeltasEnabled) {
        currentRating = rating.getOrDefault(handle, new RatingAndContestCount(OLD_INITIAL_RATING, 10_000));
        currentRating.rating = newRating;
      } else {
        currentRating = rating.getOrDefault(handle, new RatingAndContestCount(INITIAL_RATING, 0));
        currentRating.rating = FakeRatingConverter.getTrueRating(newRating, currentRating.contestCount + 1);
      }
      currentRating.contestCount++;
      rating.put(handle, currentRating);
    }
  }

  private static JSONObject toJSON(TreeMap<String, RatingAndContestCount> rating, boolean saveContestCount) {
    List<JSONObject> list = new ArrayList<>(rating.size());

    for (String handle : rating.keySet()) {
      JSONObject contestant = new JSONObject();
      contestant.put("handle", handle);
      contestant.put("rating", rating.get(handle).rating);
      if (saveContestCount) {
        int contestCount = rating.get(handle).contestCount;
        contestant.put("contest_count", contestCount);
      }
      list.add(contestant);
    }

    JSONObject contest = new JSONObject();
    contest.put("status", SUCCESSFUL_STATUS);
    contest.put(JSON_RESULTS, new JSONArray(list));

    return contest;
  }

  private static String getFileName(String contestId, String filePrefix) {
    return filePrefix + "/contest_" + contestId + ".html";
  }

  private static boolean writeToFiles(String filePrefix, TreeMap<String, RatingAndContestCount> rating,
      String contestId) {
    boolean result = true;
    String fileName = getFileName(contestId, filePrefix);
    JSONObject json = toJSON(rating, true);

    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
      json.write(writer, 2, 0);
      writer.write("\n");
    } catch (Exception ex) {
      System.err.println("Couldn't write past rating to the file\n"
          + ex.getMessage());
      result = false;
    }

    return result;
  }

  private static TreeMap<String, RatingAndContestCount> toMap(JSONObject json) {
    if (!json.has("status") || !json.has(JSON_RESULTS)) {
      return new TreeMap<>();
    }

    TreeMap<String, RatingAndContestCount> ratings = new TreeMap<>();
    JSONArray ratingsArray = json.getJSONArray(JSON_RESULTS);
    for (Object userAndRating : ratingsArray) {
      JSONObject user = (JSONObject) userAndRating;
      int contestCount = 0;
      if (user.has("contest_count")) {
        contestCount = user.getInt("contest_count");
      }
      ratings.put(user.getString("handle"), new RatingAndContestCount(user.getInt("rating"), contestCount));
    }

    return ratings;
  }

  private static boolean validate(int maxId, String filePrefix) {
    boolean result = true;
        /*
        List<Contest> contests = CodeForcesSDK.getFinishedContests(maxId, false);
        for (Contest contest : contests) {
            int contestId = contest.getId();
            String path = "file://" + getFileName("" + contestId, filePrefix);
            try {
                JSONObject json = JsonReader.read(path);
                Map<String, Integer> rating = toMap(json);
                List<RatingChange> ratingChanges = CodeForcesSDK.getRatingChanges(contestId);
                for (RatingChange ratingChange : ratingChanges) {
                    int prevRating = ratingChange.getOldRating();
                    if (prevRating != INITIAL_RATING) {
                        String handle = ratingChange.getHandle();
                        if (!rating.containsKey(handle)
                                || rating.get(handle) != prevRating) {
                            System.err.println("Wrong rating on contest: "
                                    + contestId + " handle: " + handle);
                            result = false;
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("Couldn't read file " + path + "\n" + ex.getMessage());
                result = false;
            }
        }
         */
    return result;
  }
}
