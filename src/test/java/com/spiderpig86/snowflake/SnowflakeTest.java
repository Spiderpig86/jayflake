package com.spiderpig86.snowflake;

import com.spiderpig86.snowflake.configuration.SnowflakeConfiguration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SnowflakeTest {

  @ParameterizedTest
  @MethodSource("provideSnowflakeTestArgs")
  public void constructor_fromParameterized_success(
      long timestamp,
      long dataCenter,
      long worker,
      long sequence,
      SnowflakeConfiguration snowflakeConfiguration) {
    Snowflake snowflake =
        new Snowflake(timestamp, dataCenter, worker, sequence, snowflakeConfiguration);

    Assertions.assertEquals(timestamp, snowflake.getTimeStamp());
    Assertions.assertEquals(dataCenter, snowflake.getDataCenter());
    Assertions.assertEquals(worker, snowflake.getWorker());
    Assertions.assertEquals(sequence, snowflake.getSequence());
  }

  public static Stream<Arguments> provideSnowflakeTestArgs() {
    SnowflakeConfiguration defaultConfig = SnowflakeConfiguration.getDefault();
    return Stream.of(
        Arguments.of(
            LocalDateTime.of(2020, 2, 2, 0, 1).toInstant(ZoneOffset.UTC).toEpochMilli(),
            1,
            1,
            0,
            defaultConfig),
        Arguments.of(
            Instant.now().toEpochMilli(),
            defaultConfig.getMaxDataCenter(),
            defaultConfig.getMaxWorker(),
            defaultConfig.getMaxSequence(),
            defaultConfig));
  }

  // NOTE: Validation test cases are all handled in generator. {@link Snowflake} should not be
  // exposed publicly

  @Test
  public void
      constructor_fromParameterized_noDataCenterBitsWithNonZeroDataCenter_throwsException() {
    long currentTime = System.currentTimeMillis();

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            new Snowflake(
                currentTime,
                34,
                5,
                123456,
                SnowflakeConfiguration.builder()
                    .withTimestampBits(41)
                    .withDatacenterBits(0)
                    .withWorkerBits(10)
                    .withSequenceBits(12)
                    .build()));
  }

  @ParameterizedTest
  @MethodSource("provideSnowflakeIdOnlyTestArgs")
  public void constructor_fromLong_success(
      long id, SnowflakeConfiguration configuration, Snowflake expected) {
    Snowflake snowflake = new Snowflake(id, configuration);

    Assertions.assertEquals(snowflake.getTimeStamp(), expected.getTimeStamp());
    Assertions.assertEquals(snowflake.getDataCenter(), expected.getDataCenter());
    Assertions.assertEquals(snowflake.getWorker(), expected.getWorker());
    Assertions.assertEquals(snowflake.getSequence(), expected.getSequence());
  }

  public static Stream<Arguments> provideSnowflakeIdOnlyTestArgs() {
    SnowflakeConfiguration defaultConfig = SnowflakeConfiguration.getDefault();
    return Stream.of(
        Arguments.of(
            6629523864944906240L,
            defaultConfig,
            new Snowflake(
                LocalDateTime.of(2020, 2, 2, 0, 1).toInstant(ZoneOffset.UTC).toEpochMilli(),
                1,
                1,
                0,
                defaultConfig)));
  }
}
