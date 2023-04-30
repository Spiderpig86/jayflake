package com.spiderpig86.snowflake.configuration;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SnowflakeConfigurationTest {

  @ParameterizedTest
  @MethodSource("provideBuildValidParameters")
  public void build_validParameters_success(
      int timestampBits, int datacenterBits, int workerBits, int sequenceBits) {
    SnowflakeConfiguration configuration =
        Assertions.assertDoesNotThrow(
            () ->
                SnowflakeConfiguration.builder()
                    .withTimestampBits(timestampBits)
                    .withDatacenterBits(datacenterBits)
                    .withWorkerBits(workerBits)
                    .withSequenceBits(sequenceBits)
                    .build());

    Assertions.assertEquals(timestampBits, configuration.getTimestampBits());
    Assertions.assertEquals(datacenterBits, configuration.getDatacenterBits());
    Assertions.assertEquals(workerBits, configuration.getWorkerBits());
    Assertions.assertEquals(sequenceBits, configuration.getSequenceBits());
  }

  public static Stream<Arguments> provideBuildValidParameters() {
    return Stream.of(
        Arguments.of(41, 4, 6, 12), Arguments.of(45, 0, 6, 12), Arguments.of(51, 0, 0, 12));
  }

  @ParameterizedTest
  @MethodSource("provideBuildInvalidParameters")
  public void build_invalidParameters_throwsException(
      int timestampBits,
      int datacenterBits,
      int workerBits,
      int sequenceBits,
      Class exceptionClass) {
    Assertions.assertThrows(
        exceptionClass,
        () ->
            SnowflakeConfiguration.builder()
                .withTimestampBits(timestampBits)
                .withDatacenterBits(datacenterBits)
                .withWorkerBits(workerBits)
                .withSequenceBits(sequenceBits)
                .build());
  }

  public static Stream<Arguments> provideBuildInvalidParameters() {
    return Stream.of(
        Arguments.of(-1, 46, 6, 12, IllegalArgumentException.class),
        Arguments.of(41, -1, 10, 12, IllegalArgumentException.class),
        Arguments.of(41, 11, -1, 12, IllegalArgumentException.class),
        Arguments.of(41, 4, 19, -1, IllegalArgumentException.class),
        Arguments.of(0, 45, 6, 12, IllegalArgumentException.class),
        Arguments.of(12, 45, 6, 0, IllegalArgumentException.class),
        Arguments.of(1, 1, 1, 1, IllegalArgumentException.class));
  }

  @Test
  public void getDefault_success() {
    SnowflakeConfiguration configuration =
        Assertions.assertDoesNotThrow(SnowflakeConfiguration::getDefault);

    Assertions.assertEquals(
        SnowflakeConfiguration.DEFAULT_TIMESTAMP_BITS, configuration.getTimestampBits());
    Assertions.assertEquals(
        SnowflakeConfiguration.DEFAULT_DATA_CENTER_BITS, configuration.getDatacenterBits());
    Assertions.assertEquals(
        SnowflakeConfiguration.DEFAULT_WORKER_BITS, configuration.getWorkerBits());
    Assertions.assertEquals(
        SnowflakeConfiguration.DEFAULT_SEQUENCE_BITS, configuration.getSequenceBits());
  }

  @ParameterizedTest
  @MethodSource("provideBounds")
  public void testBounds(
      SnowflakeConfiguration configuration,
      long expectedMaxTimestamp,
      long expectedMaxDatacenter,
      long expectedMaxWorker,
      long expectedMaxSequence) {
    Assertions.assertEquals(expectedMaxTimestamp, configuration.getMaxTimestamp());
    Assertions.assertEquals(expectedMaxDatacenter, configuration.getMaxDatacenter());
    Assertions.assertEquals(expectedMaxWorker, configuration.getMaxWorker());
    Assertions.assertEquals(expectedMaxSequence, configuration.getMaxSequence());
  }

  public static Stream<Arguments> provideBounds() {
    return Stream.of(
        Arguments.of(
            SnowflakeConfiguration.getDefault(),
            (1L << SnowflakeConfiguration.DEFAULT_TIMESTAMP_BITS) - 1,
            (1L << SnowflakeConfiguration.DEFAULT_DATA_CENTER_BITS) - 1,
            (1L << SnowflakeConfiguration.DEFAULT_WORKER_BITS) - 1,
            (1L << SnowflakeConfiguration.DEFAULT_SEQUENCE_BITS) - 1),
        Arguments.of(
            SnowflakeConfiguration.builder()
                .withTimestampBits(39)
                .withWorkerBits(16)
                .withSequenceBits(8)
                .build(),
            (1L << 39) - 1,
            0,
            (1L << 16) - 1,
            (1L << 8) - 1));
  }
}
