package com.spiderpig86.jayflake.lib;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
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

  static OverflowHandler overflowSpinWait(
      @Nonnull final Supplier<Long> reference, @Nonnull final Supplier<Long> actual) {
    return () -> {
      do {
        Thread.onSpinWait(); // Free up some CPU cycles
      } while (Objects.equals(actual.get(), reference.get()));
    };
  }

  static OverflowHandler overflowThrowException(@Nonnull final String fieldName) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(fieldName), "Field name must not be empty.");
    return () -> {
      throw new RuntimeException(String.format("%s has overflowed.", fieldName));
    };
  }
}
