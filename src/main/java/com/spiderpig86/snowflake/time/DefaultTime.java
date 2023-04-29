package com.spiderpig86.snowflake.time;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * Default {@link Time} class where a tick is 1 millisecond.
 */
public class DefaultTime implements Time {

    private final Instant epoch;
    private final long tickDurationMs;

    public DefaultTime(@Nonnull final Instant epoch, final long tickDurationMs) {
        this.epoch = Preconditions.checkNotNull(epoch);
        this.tickDurationMs = tickDurationMs;
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
