package com.spiderpig86.jayflake.time;

import static com.spiderpig86.jayflake.Utils.DEFAULT_EPOCH;

import java.time.Clock;
import java.time.Instant;
import javax.annotation.Nonnull;
import lombok.ToString;

/** Default {@link Time} class where a tick is 1 millisecond. */
@ToString
public class DefaultTime extends Time {

  private final long tickDurationMs;

  public DefaultTime(@Nonnull final Clock clock, @Nonnull final Instant epoch) {
    super(clock, epoch);
    this.tickDurationMs = 1;
  }

  @Override
  public long getTickDurationMs() {
    return tickDurationMs;
  }

  /**
   * Gets default instance of {@link DefaultTime} using the default epoch.
   *
   * @param clock instance of {@link Clock} used for time computation.
   * @return instance of {@link DefaultTime} with default settings.
   */
  public static Time getDefault(@Nonnull final Clock clock) {
    return new DefaultTime(clock, Instant.ofEpochMilli(DEFAULT_EPOCH));
  }
}
