package com.spiderpig86.snowflake.time;

import com.google.common.base.Preconditions;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Default {@link Time} class where a tick is 1 millisecond.
 */
@ToString
public class DefaultTime implements Time {

    private final Instant epoch;
    private final long tickDurationMs;

    public DefaultTime(@Nonnull final Instant epoch) {
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
        return System.currentTimeMillis() - this.epoch.toEpochMilli();
    }

    @Override
    public long getTickDurationMs() {
        return tickDurationMs;
    }
}
