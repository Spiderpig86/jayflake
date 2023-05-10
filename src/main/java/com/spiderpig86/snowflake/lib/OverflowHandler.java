package com.spiderpig86.snowflake.lib;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.spiderpig86.snowflake.time.Time;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;

@FunctionalInterface
public interface OverflowHandler {

  void run();

  static OverflowHandler overflowSleep(final long sleepMs) {
    return () -> {
      try {
        Thread.sleep(sleepMs);
      } catch (InterruptedException e) {
        // Ignore
      }
    };
  }

  static OverflowHandler overflowSleepJitter(
      @Nonnull ThreadLocalRandom random, final long sleepMs, final long jitterRangeMs) {
    return () -> {
      try {
        Thread.sleep(sleepMs + random.nextLong(jitterRangeMs));
      } catch (InterruptedException e) {
        // Ignore
      }
    };
  }

  static OverflowHandler overflowSpinWait(@Nonnull final Time time) {
    return () -> {
      long currentTick;
      do {
        Thread.onSpinWait(); // Free up some CPU cycles
        currentTick = time.getTick();
      } while (currentTick == time.getTick());
    };
  }

  static OverflowHandler overflowThrowException(@Nonnull final String fieldName) {
    Preconditions.checkArgument(Strings.isNullOrEmpty(fieldName), "Field name must not be empty.");
    return () -> {
      throw new RuntimeException(String.format("%s has overflowed.", fieldName));
    };
  }
}
