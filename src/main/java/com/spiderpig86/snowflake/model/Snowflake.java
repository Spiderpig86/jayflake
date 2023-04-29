package com.spiderpig86.snowflake.model;

import com.google.common.base.Preconditions;
import com.spiderpig86.snowflake.configuration.SnowflakeConfiguration;

import javax.annotation.Nonnull;

import static com.spiderpig86.snowflake.Utils.getValueWithMask;

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

    public Snowflake(final long timestamp, final long dataCenter, final long worker, final long sequence,
                     final @Nonnull SnowflakeConfiguration snowflakeConfiguration) {
        final int timestampShift =
                snowflakeConfiguration.getDataCenterBits() + snowflakeConfiguration.getWorkerBits() + snowflakeConfiguration.getSequenceBits();
        final int dataCenterShift = timestampShift - snowflakeConfiguration.getDataCenterBits();
        final int workerShift = dataCenterShift - snowflakeConfiguration.getWorkerBits();

        this.value =
                (timestamp << timestampShift) + (dataCenter << dataCenterShift) + (worker << workerShift) + sequence;
        this.snowflakeConfiguration = Preconditions.checkNotNull(snowflakeConfiguration);
    }

    public long getTimeStamp() {
        return getValueWithMask(value, snowflakeConfiguration.getTimeStampBits(),
                snowflakeConfiguration.getDataCenterBits() + snowflakeConfiguration.getWorkerBits() + snowflakeConfiguration.getSequenceBits());
    }

    public long getDataCenter() {
        return getValueWithMask(value, snowflakeConfiguration.getDataCenterBits(), snowflakeConfiguration.getWorkerBits() + snowflakeConfiguration.getSequenceBits());
    }

    public long getWorker() {
        return getValueWithMask(value, snowflakeConfiguration.getWorkerBits(), snowflakeConfiguration.getSequenceBits());
    }

    public long getSequence() {
        return getValueWithMask(value, snowflakeConfiguration.getSequenceBits(), 0);
    }
}
