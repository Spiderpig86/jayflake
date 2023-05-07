package com.spiderpig86.snowflake.time;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.time.Instant;

/**
 * Base interface for all Time metadata. Subclasses will be used to configure how the timestamp
 * generation works for the Snowflake ids, such as by being able to specify that instead of using
 * milliseconds as your unit, you can define 1 tick being equivalent to 1 second or any other value.
 * This allows anyone to customize how much time they need to store for their ids based on their own
 * requirements.
 */
public abstract class Time {

  private final Clock clock;

  Time(@Nonnull final Clock clock) {
    this.clock = Preconditions.checkNotNull(clock);
  }

  /**
   * Returns the epoch, or the start time, of when the ticks are based off of.
   *
   * @return the epoch.
   */
  abstract Instant getEpoch();

  /**
   * Returns the tick value relative to the epoch time.
   *
   * @return the current tick.
   */
  abstract long getTick();

  /**
   * Returns the length of time each tick is.
   *
   * @return the duration of a tick.
   */
  abstract long getTickDurationMs();

  Clock getClock() {
    return clock;
  }
}
