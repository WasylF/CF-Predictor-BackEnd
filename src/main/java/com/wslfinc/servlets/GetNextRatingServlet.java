package com.wslfinc.servlets;

import com.wslfinc.cf.sdk.CodeForcesSDK;
import com.wslfinc.cf.sdk.entities.additional.ContestantResult;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * @author Wsl_F
 */
public class GetNextRatingServlet extends HttpServlet {

  private static int totalGetRequests = 0;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    totalGetRequests++;
    if (totalGetRequests % 100 == 0) {
      System.out.println(System.currentTimeMillis() +
          "  GetNextRating request: " + totalGetRequests);
    }
    JSONObject json;
    try {
      int contestId = Integer.valueOf(request.getParameter("contestId"));
      List<ContestantResult> nextRating = CodeForcesSDK.getNewRatings(contestId);
      json = ContestantResult.toJSON(nextRating);
    } catch (Exception ex) {
      json = new JSONObject();
      json.put("status", "FAIL");
    }

    try {
      PrintWriter out = response.getWriter();
      out.write(json.toString());
      out.flush();
      response.setStatus(200);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }

}
