package com.wslfinc.cf.sdk;

import com.github.wslf.datastructures.cache.Cacheable;
import com.github.wslf.utils.web.WebReader;
import com.wslfinc.cf.sdk.api.CodeForcesAPI;
import com.wslfinc.cf.sdk.entities.additional.Contestant;
import com.wslfinc.helpers.web.JsonReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wslfinc.cf.sdk.Constants.*;

/**
 * @author Wsl_F
 */
public class ContestantsCached extends Cacheable<List<Contestant>, Integer> {

  private static final WebReader webReader = new WebReader();

  public ContestantsCached() {
    // TTL 4 hours
    super(10, 2, 4 * 60 * 60 * 1000);
  }

  public ArrayList<Contestant> LoadNotStoredRatings() {
    String url = PAST_RATING_URL_PREFIX + "current" + PAST_RATING_URL_SUFFIX;
    List<Contestant> fromGithub;
    try {
      fromGithub = LoadFromGithub(url);
    } catch (Exception e) {
      fromGithub = new ArrayList<>();
      System.err.println("Failed to load rating from github" + e.getMessage());
    }

    HashMap<String, Integer> ratings = new HashMap<>();
    for (Contestant contestant : fromGithub) {
      ratings.put(contestant.getHandle(), contestant.getPrevRating());
    }
    fromGithub.clear();

    List<Contestant> fromCodeforces;
    try {
      fromCodeforces = CodeForcesAPI.getInstance().getRatedList(true);
    } catch (Exception e) {
      fromCodeforces = new ArrayList<>();
      System.err.println("Failed to load rating from codeforces" + e.getMessage());
    }

    for (Contestant contestant : fromCodeforces) {
      ratings.put(contestant.getHandle(), contestant.getPrevRating());
    }
    fromCodeforces.clear();

    ArrayList<Contestant> contestants = new ArrayList<>();
    ratings.forEach((handle, rating) -> {
      contestants.add(new Contestant(handle, rating));
    });

    return contestants;
  }

  public ArrayList<Contestant> LoadFromGithub(String url) throws Exception {
    JSONObject response = JsonReader.read(url);
    if (ResponseChecker.isValid(response)) {
      JSONArray contestants = response.getJSONArray(JSON_RESULTS);

      ArrayList<Contestant> result = new ArrayList<>();
      for (Object contestant : contestants) {
        result.add(new Contestant((JSONObject) contestant));
      }
      return result;
    }

    throw new IOException("Failed to load rating from GitHib: " + response.toString());
  }

  @Override
  public List<Contestant> getValueManually(Integer key) {
    try {
      String url = PAST_RATING_URL_PREFIX + key + PAST_RATING_URL_SUFFIX;
      if (webReader.isExists(url)) {
        return LoadFromGithub(url);
      } else {
        return LoadNotStoredRatings();
      }
    } catch (Exception exception) {
      System.err.println("Failed to get all contestants on contestId: " + key);
      System.err.println(exception.getMessage());
    }

    return new ArrayList<>();
  }

}
