package com.wslfinc;

import com.github.wslf.utils.file.Writer;
import com.github.wslf.utils.web.WebReader;
import com.wslfinc.cf.EvaluateMyRatingCalculation;
import com.wslfinc.cf.sdk.CodeForcesSDK;
import com.wslfinc.cf.sdk.ContestantsCached;
import com.wslfinc.cf.sdk.entities.additional.Contestant;
import com.wslfinc.cf.sdk.entities.additional.ContestantResult;
import com.wslfinc.cf.sdk.rating.PastRatingDownloader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wslfinc.cf.sdk.Constants.*;

/**
 * @author Wsl_F
 */
public class AdditionalExecutor {

  public static void testLoadNotStoredRatings() throws Exception {
    ContestantsCached contestantsCached = new ContestantsCached();
    var ratings_new = contestantsCached.LoadNotStoredRatings();
    var ratings_github = contestantsCached.LoadFromGithub(PAST_RATING_URL_PREFIX + "current" + PAST_RATING_URL_SUFFIX);

    HashMap<String, Integer> ratings_new_map = new HashMap<>();
    for (Contestant contestant : ratings_new) {
      ratings_new_map.put(contestant.getHandle(), contestant.getPrevRating());
    }

    int incorrect = 0;
    int missed = 0;
    for (Contestant contestant : ratings_github) {
      if (ratings_new_map.containsKey(contestant.getHandle())) {
        if (ratings_new_map.get(contestant.getHandle()) != contestant.getPrevRating()) {
          if (incorrect < 10) {
            System.err.println("Incorrect rating: " + contestant.getHandle() + " " + contestant.getPrevRating() + "\t" +
                               ratings_new_map.get(contestant.getHandle()));
          }
          incorrect++;
        }
      } else {
        if (missed < 10) {
          System.err.println("Missing rating: " + contestant.getHandle());
        }
        missed++;
      }
    }
  }

  public static void main(String[] args) throws Exception {
    testLoadNotStoredRatings();

//    //args = new String[]{"getPastRating", "767"};
//    args = new String[]{"getPastRating", "1100", "1028"};
//    //args = new String[]{"testRating", "908", "908"};//592
//    //args = new String[]{"matchesIdToNames", "false"};
//    //args = new String[]{"calcGetNext", "1037", "1037"};
//
//    switch (args[0]) {
//      case "getPastRating":
//        getPastRating(args);
//        break;
//      case "getNewRating":
//        getNewRating(args);
//        break;
//      case "testRating":
//        testMyRating(args);
//        break;
//      case "matchesIdToNames":
//        matchesIdToNames(args);
//        break;
//      case "calcGetNext":
//        calcGetNext(args);
//        break;
//      default:
//        System.out.println("Option \"" + args[0] + "\" hasn't recognized");
//    }
  }

  public static void getPastRating(String[] args) {
    int maxId = Integer.valueOf(args[1]);
    int loadFromId = args.length == 3 ? Integer.valueOf(args[2]) : -1;
    boolean succes = PastRatingDownloader.getRatingBeforeContest(loadFromId, maxId, PATH_TO_PROJECT + "/contests");
    if (succes) {
      System.out.println("Rating from past contests was successfully downloaded and processed!");
    } else {
      System.out.println("There were some troubles with dowloading and processing past rating");
    }
  }

  public static void getNewRating(String[] args) {
    int contestId = Integer.valueOf(args[1]);
    List<ContestantResult> newRatings = CodeForcesSDK.getNewRatings(contestId);
    System.out.println("Handle Rank Seed PrevRating NextRating");
    for (ContestantResult cr : newRatings) {
      System.out.println(cr.getHandle() + " " + cr.getRank() + " " + cr.getSeed() + " " + cr.getPrevRating() + " " +
                         cr.getNextRating());
    }
  }

  private static void testMyRating(String[] args) {
    int minId = Integer.valueOf(args[1]);
    int maxId = Integer.valueOf(args[2]);
    EvaluateMyRatingCalculation.calculateRatingDiff(minId, maxId);
  }

  private static void matchesIdToNames(String[] args) {
    boolean gym = Boolean.getBoolean(args[1]);
    List<String> contests = CodeForcesSDK.getContestNames(gym);
    List<JSONObject> list = new ArrayList<>(contests.size());
    for (int contestId = contests.size() - 1; contestId > 0; contestId--) {
      JSONObject json = new JSONObject();
      json.put("contestId", contestId);
      json.put("name", contests.get(contestId));
      list.add(json);
    }

    JSONObject contestNames = new JSONObject();
    contestNames.put("status", "OK");
    contestNames.put("result", new JSONArray(list));

    String fileName = "contests/contestNames.json";
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
      contestNames.write(writer);
      writer.write("\n");
    } catch (Exception ex) {
      System.err.println("Couldn't write contestNames\n" + ex.getMessage());
    }
  }

  private static void calcGetNext(String[] args) {
    int minId = Integer.valueOf(args[1]);
    int maxId = Integer.valueOf(args[2]);
    WebReader reader = new WebReader();
    Writer writer = new Writer();

    for (int id = minId; id <= maxId; id++) {
      try {
        String json = reader.read("http://cf-predictor-compute.herokuapp.com/GetNextRatingServlet?contestId=" + id);
        String fileName = "nextRating/contest_" + id + ".html";
        writer.write(json, fileName);
        if (json.length() > 1000) {
          System.out.println("GetNextRating for contest " + id + " has been written successful");
        } else {
          System.err.println("Error while processing contest " + id);
        }
      } catch (Exception ex) {
        System.err.println("Couldn't process contest " + id);
      }
    }

  }

}
