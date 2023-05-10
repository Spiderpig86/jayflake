package com.spiderpig86.jayflake.configuration;

import com.spiderpig86.jayflake.lib.OverflowStrategy;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GeneratorConfigurationTest {

  @ParameterizedTest
  @MethodSource("provideBuildValidInputs")
  public void build_validInputs_succeeds(
      long dataCenter, long worker, OverflowStrategy overflowStrategy) {
    GeneratorConfiguration configuration =
        Assertions.assertDoesNotThrow(
            () ->
                GeneratorConfiguration.builder()
                    .withDataCenter(dataCenter)
                    .withWorker(worker)
                    .withOverflowStrategy(overflowStrategy)
                    .build());

    Assertions.assertEquals(dataCenter, configuration.getDataCenter());
    Assertions.assertEquals(worker, configuration.getWorker());
  }

  private static Stream<Arguments> provideBuildValidInputs() {
    return Stream.of(
        Arguments.of(0L, 0L, OverflowStrategy.SLEEP),
        Arguments.of(12L, 34L, OverflowStrategy.SLEEP));
  }

  @ParameterizedTest
  @MethodSource("provideBuildInvalidInputs")
  public void build_invalidInputs_throwsException(
      long dataCenter, long worker, OverflowStrategy overflowStrategy, Class exceptionClass) {
    Assertions.assertThrows(
        exceptionClass,
        () ->
            GeneratorConfiguration.builder()
                .withDataCenter(dataCenter)
                .withWorker(worker)
                .withOverflowStrategy(overflowStrategy)
                .build());
  }

  private static Stream<Arguments> provideBuildInvalidInputs() {
    return Stream.of(
        Arguments.of(-1, 0, OverflowStrategy.SLEEP, IllegalArgumentException.class),
        Arguments.of(0, -1, OverflowStrategy.SLEEP, IllegalArgumentException.class));
  }

  @Test
  public void getDefault_success() {
    GeneratorConfiguration configuration =
        Assertions.assertDoesNotThrow(GeneratorConfiguration::getDefault);

    Assertions.assertEquals(0, configuration.getDataCenter());
    Assertions.assertEquals(0, configuration.getWorker());
  }
}
