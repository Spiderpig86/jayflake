package com.spiderpig86.jayflake.configuration;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.ToString;

/** Configuration for the Snowflake id itself. These settings determine the format of the id. */
@Getter
@ToString
public class SnowflakeConfiguration {
  /* Default bit configuration */
  @VisibleForTesting static final int BIT_LENGTH = 63; // 64 bits, but sign bit is ignored
  @VisibleForTesting static final int DEFAULT_TIMESTAMP_BITS = 41;
  @VisibleForTesting static final int DEFAULT_DATA_CENTER_BITS = 4;
  @VisibleForTesting static final int DEFAULT_WORKER_BITS = 6;
  @VisibleForTesting static final int DEFAULT_SEQUENCE_BITS = 12;

  private final int timestampBits;
  private final int dataCenterBits;
  private final int workerBits;
  private final int sequenceBits;

  private SnowflakeConfiguration(
      final int timestampBits,
      final int dataCenterBits,
      final int workerBits,
      final int sequenceBits) {
    this.timestampBits = timestampBits;
    this.dataCenterBits = dataCenterBits;
    this.workerBits = workerBits;
    this.sequenceBits = sequenceBits;
  }

  // Helper functions to determine max values
  public long getMaxTimestamp() {
    return getMaxValueWithBits(this.timestampBits);
  }

  public long getMaxDataCenter() {
    return getMaxValueWithBits(this.dataCenterBits);
  }

  public long getMaxWorker() {
    return getMaxValueWithBits(this.workerBits);
  }

  public long getMaxSequence() {
    return getMaxValueWithBits(this.sequenceBits);
  }

  private long getMaxValueWithBits(final int bits) {
    return (1L << bits) - 1;
  }

  public static class Builder {
    int timestampBits;
    int datacenterBits;
    int workerBits;
    int sequenceBits;

    public Builder withTimestampBits(final int timestampBits) {
      this.timestampBits = timestampBits;
      return this;
    }

    public Builder withDatacenterBits(final int datacenterBits) {
      this.datacenterBits = datacenterBits;
      return this;
    }

    public Builder withWorkerBits(final int workerBits) {
      this.workerBits = workerBits;
      return this;
    }

    public Builder withSequenceBits(final int sequenceBits) {
      this.sequenceBits = sequenceBits;
      return this;
    }

    public SnowflakeConfiguration build() {
      validate();
      return new SnowflakeConfiguration(timestampBits, datacenterBits, workerBits, sequenceBits);
    }

    private void validate() {
      Preconditions.checkArgument(
          timestampBits >= 1 && timestampBits < BIT_LENGTH, "Given timeStampBits is invalid");
      Preconditions.checkArgument(
          datacenterBits >= 0 && datacenterBits < BIT_LENGTH, "Given dataCenterBits is invalid");
      Preconditions.checkArgument(
          workerBits >= 0 && workerBits < BIT_LENGTH, "Given workerBits is invalid");
      Preconditions.checkArgument(
          sequenceBits >= 1 && sequenceBits < BIT_LENGTH, "Given sequenceBits is invalid");

      final int totalLength = timestampBits + datacenterBits + workerBits + sequenceBits;
      Preconditions.checkArgument(
          totalLength == BIT_LENGTH,
          String.format("Given bit lengths don't add up to %d", BIT_LENGTH));
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static SnowflakeConfiguration getDefault() {
    return new Builder()
        .withTimestampBits(DEFAULT_TIMESTAMP_BITS)
        .withDatacenterBits(DEFAULT_DATA_CENTER_BITS)
        .withWorkerBits(DEFAULT_WORKER_BITS)
        .withSequenceBits(DEFAULT_SEQUENCE_BITS)
        .build();
  }
}
