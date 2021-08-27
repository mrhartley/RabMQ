package com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils;

import static com.appiancorp.cs.plugin.rabbitMQServices.smartservice.utils.UtilConstants.*;

public final class TimeUtils {

  public void TimeUtils() {
  }

  public static boolean isTimeValid(long timeLimit) {

    // Check time limits are within range
    if (timeLimit < LIMITS_LOWER_TIME_LIMIT) {
      return false;
    } else if (timeLimit > LIMITS_UPPER_TIME_LIMIT) {
      return false;
    } else {
      return true;
    }
  }

  public static Long getMilliseconds(Long timeMins) {
    if (timeMins != null) {
      return timeMins * 60 * 1000;
    } else {
      return null;
    }
  }

}
