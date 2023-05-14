package com.spiderpig86.jayflake;

import static com.spiderpig86.jayflake.Utils.getValueWithMask;

import com.google.common.base.Preconditions;
import com.spiderpig86.jayflake.configuration.SnowflakeConfiguration;
import javax.annotation.Nonnull;

/** Wrapper class that represents a Snowflake id. */
public class Snowflake {
  private final long value;
  private final SnowflakeConfiguration snowflakeConfiguration;

  Snowflake(final long value, final @Nonnull SnowflakeConfiguration snowflakeConfiguration) {
    this.value = value;
    this.snowflakeConfiguration = Preconditions.checkNotNull(snowflakeConfiguration);
  }

  Snowflake(
      final long timestamp,
      final long dataCenter,
      final long worker,
      final long sequence,
      final @Nonnull SnowflakeConfiguration snowflakeConfiguration) {
    final int timestampShift =
        snowflakeConfiguration.getDataCenterBits()
            + snowflakeConfiguration.getWorkerBits()
            + snowflakeConfiguration.getSequenceBits();
    final int dataCenterShift = timestampShift - snowflakeConfiguration.getDataCenterBits();
    final int workerShift = dataCenterShift - snowflakeConfiguration.getWorkerBits();

    // If we reach here, this means the SnowflakeGenerator is not doing its job
    Preconditions.checkArgument(
        timestamp <= snowflakeConfiguration.getMaxTimestamp(),
        "Provided timestamp exceeds " + "Snowflake max timestamp");
    Preconditions.checkArgument(
        dataCenter <= snowflakeConfiguration.getMaxDataCenter(),
        "Provided timestamp exceeds " + "Snowflake max data center");
    Preconditions.checkArgument(
        worker <= snowflakeConfiguration.getMaxWorker(),
        "Provided timestamp exceeds " + "Snowflake max worker");
    Preconditions.checkArgument(
        sequence <= snowflakeConfiguration.getMaxSequence(),
        "Provided timestamp exceeds " + "Snowflake max sequence");

    this.value =
        (timestamp << timestampShift)
            + (dataCenter << dataCenterShift)
            + (worker << workerShift)
            + sequence;
    this.snowflakeConfiguration = Preconditions.checkNotNull(snowflakeConfiguration);
  }

  /**
   * Returns raw Snowflake id.
   *
   * @return {@link Snowflake} in its long form.
   */
  public long value() {
    return value;
  }

  public long getTimeStamp() {
    return getValueWithMask(
        value,
        snowflakeConfiguration.getTimestampBits(),
        snowflakeConfiguration.getDataCenterBits()
            + snowflakeConfiguration.getWorkerBits()
            + snowflakeConfiguration.getSequenceBits());
  }

  public long getDataCenter() {
    return getValueWithMask(
        value,
        snowflakeConfiguration.getDataCenterBits(),
        snowflakeConfiguration.getWorkerBits() + snowflakeConfiguration.getSequenceBits());
  }

  public long getWorker() {
    return getValueWithMask(
        value, snowflakeConfiguration.getWorkerBits(), snowflakeConfiguration.getSequenceBits());
  }

  public long getSequence() {
    return getValueWithMask(value, snowflakeConfiguration.getSequenceBits(), 0);
  }
}
