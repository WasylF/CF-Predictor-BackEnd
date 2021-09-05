package com.wslfinc.cf.sdk;

/**
 * @author Wsl_F
 */
public class Constants {

  /**
   * Delay before send request to Codeforces. Value in milliseconds.
   */
  public static final int API_DELAY_MS = 300;

  /**
   * Status of successful codeforces JSON-response.
   */
  public static final String SUCCESSFUL_STATUS = "OK";

  /**
   * JSON tag of codeforces API query results
   */
  public static final String JSON_RESULTS = "result";

  public static final String JSON_STATUS = "status";

  public static final String API_PREFIX = "http://codeforces.com/api";

  public static final int NEGATIVE_INFINITY = -1_000_000_000;

  public static final String PAST_RATING_URL_PREFIX
      = "https://codeforcescontests.github.io/RatingAfterRounds/contests/contest_";

  public static final String PAST_RATING_URL_SUFFIX = ".html";

  public static final int INITIAL_RATING = 1400;

  public static final int MAXIMAL_CONTESTANTS = 10_000;

  public static final int MAX_CONTEST_ID = 10_0000;

  public static final int MAX_RATING_EDUCATIONAL_PARTICIPANT = 2100;

  public static final int[] FAKE_DELTAS = new int[]{500, 850, 1100, 1250, 1350, 1400};
}
