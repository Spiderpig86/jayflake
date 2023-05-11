package com.spiderpig86.jayflake.time;

import static com.spiderpig86.jayflake.Utils.DEFAULT_EPOCH;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import lombok.ToString;

/** Default {@link Time} class where a tick is 1 millisecond. */
@ToString
public class DefaultTime extends Time {

  private final long tickDurationMs;

  public DefaultTime(@Nonnull final Clock clock, @Nonnull final Instant epoch) {
    super(clock, epoch);
    this.tickDurationMs = TimeUnit.SECONDS.toMillis(1);
  }

  @Override
  public long getTickDurationMs() {
    return tickDurationMs;
  }

  public static Time getDefault(@Nonnull final Clock clock) {
    return new DefaultTime(clock, Instant.ofEpochMilli(DEFAULT_EPOCH));
  }
}
