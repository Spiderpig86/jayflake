package com.spiderpig86.snowflake.configuration;

import java.time.Instant;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GeneratorConfigurationTest {

  private static final long EPOCH_SECOND = 1222839884;

  @ParameterizedTest
  @MethodSource("provideBuildValidInputs")
  public void build_validInputs_succeeds(Instant epoch, long dataCenter, long worker) {
    GeneratorConfiguration configuration =
        Assertions.assertDoesNotThrow(
            () ->
                GeneratorConfiguration.builder()
                    .withEpoch(epoch)
                    .withDataCenter(dataCenter)
                    .withWorker(worker)
                    .build());

    Assertions.assertEquals(epoch, configuration.getEpoch());
    Assertions.assertEquals(dataCenter, configuration.getDataCenter());
    Assertions.assertEquals(worker, configuration.getWorker());
  }

  private static Stream<Arguments> provideBuildValidInputs() {
    return Stream.of(
        Arguments.of(Instant.now(), 0L, 0L),
        Arguments.of(Instant.now(), 12L, 34L),
        Arguments.of(Instant.ofEpochMilli(EPOCH_SECOND), 0L, 0L));
  }

  @ParameterizedTest
  @MethodSource("provideBuildInvalidInputs")
  public void build_invalidInputs_throwsException(
      Instant epoch, long dataCenter, long worker, Class exceptionClass) {
    Assertions.assertThrows(
        exceptionClass,
        () ->
            GeneratorConfiguration.builder()
                .withEpoch(epoch)
                .withDataCenter(dataCenter)
                .withWorker(worker)
                .build());
  }

  private static Stream<Arguments> provideBuildInvalidInputs() {
    return Stream.of(
        Arguments.of(Instant.now(), -1, 0, IllegalArgumentException.class),
        Arguments.of(Instant.now(), 0, -1, IllegalArgumentException.class));
  }

  @Test
  public void getDefault_success() {
    GeneratorConfiguration configuration =
        Assertions.assertDoesNotThrow(
            () -> GeneratorConfiguration.getDefault(Instant.ofEpochSecond(EPOCH_SECOND)));

    Assertions.assertEquals(EPOCH_SECOND, configuration.getEpoch().getEpochSecond());
    Assertions.assertEquals(0, configuration.getDataCenter());
    Assertions.assertEquals(0, configuration.getWorker());
  }
}
