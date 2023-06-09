package com.spiderpig86.jayflake.time;

import com.google.common.base.Preconditions;
import java.time.Clock;
import java.time.Instant;
import javax.annotation.Nonnull;

/**
 * Base interface for all Time metadata. Subclasses will be used to configure how the timestamp
 * generation works for the Snowflake ids, such as by being able to specify that instead of using
 * milliseconds as your unit, you can define 1 tick being equivalent to 1 second or any other value.
 * This allows anyone to customize how much time they need to store for their ids based on their own
 * requirements.
 */
public abstract class Time {

  private final Clock clock;
  private final Instant epoch;

  Time(@Nonnull final Clock clock, @Nonnull final Instant epoch) {
    this.clock = Preconditions.checkNotNull(clock);
    this.epoch = Preconditions.checkNotNull(epoch);

    Preconditions.checkArgument(
        clock.millis() >= epoch.toEpochMilli(), "Epoch is before current time");
  }

  /**
   * Returns the tick value relative to the epoch time.
   *
   * @return the current tick.
   */
  public long getTick() {
    return (getClock().millis() - getEpoch().toEpochMilli()) / getTickDurationMs();
  }

  /**
   * Returns the length of time each tick is.
   *
   * @return the duration of a tick.
   */
  public abstract long getTickDurationMs();

  /**
   * Returns the epoch, or the start time, of when the ticks are based off of.
   *
   * @return the epoch.
   */
  @Nonnull
  public Instant getEpoch() {
    return epoch;
  }

  Clock getClock() {
    return clock;
  }
}
