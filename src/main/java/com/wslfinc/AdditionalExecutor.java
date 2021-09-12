package com.wslfinc;

import com.github.wslf.utils.file.Writer;
import com.github.wslf.utils.web.WebReader;
import com.wslfinc.cf.EvaluateMyRatingCalculation;
import com.wslfinc.cf.sdk.CodeForcesSDK;
import com.wslfinc.cf.sdk.entities.additional.ContestantResult;
import com.wslfinc.cf.sdk.rating.PastRatingDownloader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Wsl_F
 */
public class AdditionalExecutor {

  public static void main(String[] args) throws Exception {
    //args = new String[]{"getPastRating", "767"};
    args = new String[]{"getPastRating", "1700", "1515", "/home/wasylf/Projects/RatingStorage/contests"};
    // args = new String[]{"testRating", "908", "908"}; //592
    // args = new String[]{"matchesIdToNames", "false"};
    args = new String[]{"calcGetNext", "1522", "1522", "/home/wasylf/Projects/RatingStorage/next_rating"};

    switch (args[0]) {
      case "getPastRating":
        getPastRating(args);
        break;
      case "getNewRating":
        getNewRating(args);
        break;
      case "testRating":
        testMyRating(args);
        break;
      case "matchesIdToNames":
        matchesIdToNames(args);
        break;
      case "calcGetNext":
        calcGetNext(args);
        break;
      default:
        System.out.println("Option \"" + args[0] + "\" hasn't recognized");
    }
  }

  public static void getPastRating(String[] args) {
    if (args.length != 4) {
      throw new IllegalArgumentException(
          "Arguments should have 4 values: getPastRating, max contest id, id of contest from which to load initial rating, path to where rating data is stored.");
    }
    int maxId = Integer.parseInt(args[1]);
    int loadFromId = Integer.parseInt(args[2]);
    String path = args[3];
    boolean success
        = PastRatingDownloader.getRatingBeforeContest(loadFromId, maxId, path);
    if (success) {
      System.out.println("Rating from past contests was successfully downloaded and processed!");
    } else {
      System.out.println("There were some troubles with downloading and processing past rating");
    }
  }

  public static void getNewRating(String[] args) {
    int contestId = Integer.parseInt(args[1]);
    List<ContestantResult> newRatings = CodeForcesSDK.getNewRatings(contestId);
    System.out.println("Handle Rank Seed PrevRating NextRating");
    for (ContestantResult cr : newRatings) {
      System.out.println(cr.getHandle() + " " + cr.getRank() + " "
          + cr.getSeed() + " " + cr.getPrevRating() + " " + cr.getNextRating());
    }
  }

  private static void testMyRating(String[] args) {
    int minId = Integer.parseInt(args[1]);
    int maxId = Integer.parseInt(args[2]);
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
      System.err.println("Couldn't write contestNames\n"
          + ex.getMessage());
    }
  }

  private static void calcGetNext(String[] args) {
    if (args.length != 4) {
      throw new IllegalArgumentException(
          "Arguments should have 4 values: calcGetNext, min contest id, max contest id, path to where rating changes should be written.");
    }
    int minId = Integer.parseInt(args[1]);
    int maxId = Integer.parseInt(args[2]);
    String path = args[3];
    WebReader reader = new WebReader();
    Writer writer = new Writer();

    for (int id = minId; id <= maxId; id++) {
      try {
        String json = reader.read("http://cf-predictor-compute.herokuapp.com/GetNextRatingServlet?contestId=" + id);
        String fileName = path + "/contest_" + id + ".html";
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
