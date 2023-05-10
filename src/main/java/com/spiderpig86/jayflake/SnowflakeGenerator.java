package com.spiderpig86.jayflake;

import com.google.common.base.Preconditions;
import com.spiderpig86.jayflake.configuration.GeneratorConfiguration;
import com.spiderpig86.jayflake.configuration.SnowflakeConfiguration;
import com.spiderpig86.jayflake.lib.OverflowHandler;
import com.spiderpig86.jayflake.time.DefaultTime;
import com.spiderpig86.jayflake.time.Time;
import java.time.Clock;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** A thread-safe class for generating Snowflake ids. */
public class SnowflakeGenerator {

  private static final Logger logger = Logger.getLogger(SnowflakeGenerator.class.getName());
  private static final Lock lock = new ReentrantLock();
  private static final ThreadLocalRandom random = ThreadLocalRandom.current();

  // TODO move to some config object
  private static final long DEFAULT_TIMER_SLEEP_MS = 100L;
  private static final long DEFAULT_JITTER_MS = 500L;

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
    long timestamp = time.getTick();
    if (timestamp < previousTimestamp) {
      // Current timestamp should not be behind the previous recorded one, throw exception
      throw new IllegalStateException("Current timestamp is behind previous timestamp");
    }

    if (previousTimestamp == timestamp) {
      if (sequence >= maxSequence) {
        // Handle overflow
        logger.warning(
            String.format(
                "Reached max sequence value of %d. Attempting to generate id again.", maxSequence));
        handleSequenceOverflow();

        return generateSnowflake();
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

  /** Potentially blocking method depending on the overflow strategy configured. */
  private void handleSequenceOverflow() {
    switch (generatorConfiguration.getOverflowStrategy()) {
      case SLEEP -> OverflowHandler.overflowSleep(DEFAULT_TIMER_SLEEP_MS).run();
      case SLEEP_WITH_JITTER -> OverflowHandler.overflowSleepJitter(
              random, DEFAULT_TIMER_SLEEP_MS, DEFAULT_JITTER_MS)
          .run();
      case SPIN_WAIT -> OverflowHandler.overflowSpinWait(() -> previousTimestamp, time::getTick)
          .run();
      case THROW_EXCEPTION -> OverflowHandler.overflowThrowException("sequence").run();
      default -> throw new IllegalArgumentException("Unsupported overflow strategy provided");
    }
  }

  private static Clock getClock() {
    return Clock.systemDefaultZone();
  }
}
