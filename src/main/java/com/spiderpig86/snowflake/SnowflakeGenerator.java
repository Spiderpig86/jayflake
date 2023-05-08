package com.spiderpig86.snowflake;

import static com.spiderpig86.snowflake.Utils.getValueWithMask;

import com.google.common.base.Preconditions;
import com.spiderpig86.snowflake.configuration.GeneratorConfiguration;
import com.spiderpig86.snowflake.configuration.SnowflakeConfiguration;
import com.spiderpig86.snowflake.time.DefaultTime;
import com.spiderpig86.snowflake.time.Time;
import java.time.Clock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** A thread-safe class for generating Snowflake ids. */
public class SnowflakeGenerator {

  private static final Logger logger = Logger.getLogger(SnowflakeGenerator.class.getName());
  private static final Lock lock = new ReentrantLock();

  private final SnowflakeConfiguration snowflakeConfiguration;
  private final GeneratorConfiguration generatorConfiguration;
  private final Time time;

  private final long maxSequence;

  private long previousTimestamp = -1;
  private long sequence = 0;

  private SnowflakeGenerator(
      @Nonnull final SnowflakeConfiguration snowflakeConfiguration,
      @Nonnull final GeneratorConfiguration generatorConfiguration,
      @Nonnull final Time time) {
    this.snowflakeConfiguration = Preconditions.checkNotNull(snowflakeConfiguration);
    this.generatorConfiguration = Preconditions.checkNotNull(generatorConfiguration);
    this.time = Preconditions.checkNotNull(time);

    validateConfigurations();

    this.maxSequence = snowflakeConfiguration.getMaxSequence();
  }

  public static SnowflakeGenerator createDefault() {
    return new SnowflakeGenerator(
        SnowflakeConfiguration.getDefault(),
        GeneratorConfiguration.getDefault(),
        DefaultTime.getDefault(getClock()));
  }

  public static SnowflakeGenerator create(
      @Nonnull final SnowflakeConfiguration snowflakeConfiguration,
      @Nonnull final GeneratorConfiguration generatorConfiguration,
      @Nonnull final Time time) {
    return new SnowflakeGenerator(snowflakeConfiguration, generatorConfiguration, time);
  }

  @Nullable
  public Snowflake next() {
    try {
      lock.lock();
      return generateSnowflake();
    } finally {
      lock.unlock();
    }
  }

  private Snowflake generateSnowflake() {
    long timestamp =
        getValueWithMask(getClock().millis(), snowflakeConfiguration.getTimestampBits(), 0);
    if (timestamp < previousTimestamp) {
      // Current timestamp should not be behind the previous recorded one, throw exception
      throw new IllegalStateException("Current timestamp is behind previous timestamp");
    }

    if (previousTimestamp == timestamp) {
      if (sequence >= maxSequence) {
        // Handle overflow
        // TODO other ways of handling this like throwing exception
        // TODO set max recursive depth for retries with private next(retries) method
        logger.warning(
            String.format(
                "Reached max sequence value of %d. Attempting to generate id again.", maxSequence));
        return next();
      }

      // Times are the same, increment the sequence
      sequence++;
    } else {
      // Reset sequence for different timestamp
      sequence = 0;
    }

    previousTimestamp = timestamp;

    return new Snowflake(
        timestamp,
        generatorConfiguration.getDataCenter(),
        generatorConfiguration.getWorker(),
        sequence,
        snowflakeConfiguration);
  }

  private void validateConfigurations() {
    // Ensure that the provided GeneratorConfiguration values are in range
    Preconditions.checkArgument(
        time.getEpoch().toEpochMilli() >= 0
            && time.getEpoch().toEpochMilli() <= snowflakeConfiguration.getMaxTimestamp(),
        "Provided timestamp value is out of bounds.");
    Preconditions.checkArgument(
        generatorConfiguration.getDataCenter() >= 0
            && generatorConfiguration.getDataCenter() <= snowflakeConfiguration.getMaxDataCenter(),
        "Provided data center value is out of bounds.");
    Preconditions.checkArgument(
        generatorConfiguration.getWorker() >= 0
            && generatorConfiguration.getWorker() <= snowflakeConfiguration.getMaxWorker(),
        "Provided worker value is out of bounds.");
  }

  private static Clock getClock() {
    return Clock.systemDefaultZone();
  }
}
