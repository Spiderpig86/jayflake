package com.spiderpig86.snowflake.time;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import lombok.ToString;

/** Default {@link Time} class where a tick is 1 millisecond. */
@ToString
public class DefaultTime extends Time {

  @VisibleForTesting static final long DEFAULT_EPOCH = 1577779200000L;

  private final Instant epoch;
  private final long tickDurationMs;

  public DefaultTime(@Nonnull final Clock clock, @Nonnull final Instant epoch) {
    super(clock);
    this.epoch = Preconditions.checkNotNull(epoch);
    this.tickDurationMs = TimeUnit.SECONDS.toMillis(1);
  }

  @Nonnull
  @Override
  public Instant getEpoch() {
    return this.epoch;
  }

  @Override
  public long getTick() {
    return getClock().millis() - this.epoch.toEpochMilli();
  }

  @Override
  public long getTickDurationMs() {
    return tickDurationMs;
  }

  public static Time getDefault(@Nonnull final Clock clock) {
    return new DefaultTime(clock, Instant.ofEpochMilli(DEFAULT_EPOCH));
  }
}
