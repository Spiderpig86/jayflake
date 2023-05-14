package com.spiderpig86.jayflake.lib;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

/** Default interface for functions that handle overflow values. */
@FunctionalInterface
public interface OverflowHandler {

  void run();

  /**
   * Sleeps for some provided duration in milliseconds.
   *
   * @param sleepMs milliseconds to sleep.
   */
  static OverflowHandler overflowSleep(final long sleepMs) {
    return () -> {
      try {
        Thread.sleep(sleepMs);
      } catch (InterruptedException e) {
        // Ignore
      }
    };
  }

  /**
   * Sleeps for some provided duration in milliseconds plus some jitter. A random value is
   * determined using {@link ThreadLocalRandom} and {@code jitterRangeMs}.
   *
   * @param random a thread-safe class for RNG.
   * @param sleepMs milliseconds to sleep by default.
   * @param jitterRangeMs max additional milliseconds to sleep to add entropy.
   */
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

  /**
   * Provide system hint that it is in a spin loop. This will permit the underlying hardware to
   * allocate resources to higher priority task while the condition is met.
   *
   * @param reference Supplier that returns the reference tick we want to compare to.
   * @param actual Supplier that returns the current tick.
   */
  static OverflowHandler overflowSpinWait(
      @Nonnull final Supplier<Long> reference, @Nonnull final Supplier<Long> actual) {
    return () -> {
      do {
        Thread.onSpinWait(); // Free up some CPU cycles
      } while (Objects.equals(actual.get(), reference.get()));
    };
  }

  /**
   * Throws an exception if a variable has overflowed its value.
   *
   * @param fieldName name of field to track overflowing.
   */
  static OverflowHandler overflowThrowException(@Nonnull final String fieldName) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(fieldName), "Field name must not be empty.");
    return () -> {
      throw new RuntimeException(String.format("%s has overflowed.", fieldName));
    };
  }
}
