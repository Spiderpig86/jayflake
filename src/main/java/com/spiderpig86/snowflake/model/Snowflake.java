package com.spiderpig86.snowflake.model;

import com.google.common.base.Preconditions;
import com.spiderpig86.snowflake.configuration.SnowflakeConfiguration;

import javax.annotation.Nonnull;

/**
 * Wrapper class that represents a Snowflake id.
 */
public class Snowflake {
    private final long value;
    private final SnowflakeConfiguration snowflakeConfiguration;

    public Snowflake(final long value, final @Nonnull SnowflakeConfiguration snowflakeConfiguration) {
        this.value = value;
        this.snowflakeConfiguration = Preconditions.checkNotNull(snowflakeConfiguration);
    }

    public long getTimeStamp() {
        return getValueWithMask(snowflakeConfiguration.getTimeStampBits(),
                snowflakeConfiguration.getDataCenterBits() + snowflakeConfiguration.getWorkerBits() + snowflakeConfiguration.getSequenceBits());
    }

    public long getDataCenter() {
        return getValueWithMask(snowflakeConfiguration.getDataCenterBits(), snowflakeConfiguration.getWorkerBits() + snowflakeConfiguration.getSequenceBits());
    }

    public long getWorker() {
        return getValueWithMask(snowflakeConfiguration.getWorkerBits(), snowflakeConfiguration.getSequenceBits());
    }

    public long getSequence() {
        return getValueWithMask(snowflakeConfiguration.getSequenceBits(), 0);
    }

    private long getValueWithMask(final int maskLength, final int maskOffset) {
        long mask = ((1L << maskLength) - 1) << maskOffset;
        return (value & mask) >> maskOffset;
    }
}
