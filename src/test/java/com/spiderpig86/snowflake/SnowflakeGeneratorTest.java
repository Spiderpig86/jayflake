package com.spiderpig86.snowflake;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.spiderpig86.snowflake.configuration.GeneratorConfiguration;
import com.spiderpig86.snowflake.configuration.SnowflakeConfiguration;
import com.spiderpig86.snowflake.time.DefaultTime;
import com.spiderpig86.snowflake.time.Time;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class SnowflakeGeneratorTest {
  private static final long EPOCH_MILLIS = Instant.ofEpochSecond(1580544000).toEpochMilli();
  private static final long ANY_VALID_NUMBER = 1;

  private Clock clock;

  @BeforeAll
  public void start() {
    clock = Clock.systemDefaultZone();
  }

  @ParameterizedTest
  @MethodSource("provide_create_invalidArgs_throwsException")
  public void create_invalidArgs_throwsException(
      SnowflakeConfiguration snowflakeConfiguration,
      GeneratorConfiguration generatorConfiguration,
      Time time) {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> SnowflakeGenerator.create(snowflakeConfiguration, generatorConfiguration, time));
  }

  public Stream<Arguments> provide_create_invalidArgs_throwsException() {
    return Stream.of(
        // Timestamp too large
        Arguments.of(
            SnowflakeConfiguration.builder()
                .withTimestampBits(11)
                .withDatacenterBits(32)
                .withWorkerBits(8)
                .withSequenceBits(12)
                .build(),
            GeneratorConfiguration.getDefault(),
            DefaultTime.getDefault(clock),
            // Data center is too large
            Arguments.of(
                SnowflakeConfiguration.builder()
                    .withTimestampBits(41)
                    .withDatacenterBits(2)
                    .withWorkerBits(8)
                    .withSequenceBits(12)
                    .build(),
                GeneratorConfiguration.builder()
                    .withDataCenter(6L)
                    .withWorker(ANY_VALID_NUMBER)
                    .build(),
                new DefaultTime(clock, Instant.now())),
            // Data center is too large
            Arguments.of(
                SnowflakeConfiguration.builder()
                    .withTimestampBits(41)
                    .withDatacenterBits(8)
                    .withWorkerBits(2)
                    .withSequenceBits(12)
                    .build(),
                GeneratorConfiguration.builder()
                    .withDataCenter(ANY_VALID_NUMBER)
                    .withWorker(6L)
                    .build(),
                new DefaultTime(clock, Instant.now()))));
  }

  @ParameterizedTest
  @MethodSource("provide_next_generators_success")
  public void next_generators_success(SnowflakeGenerator generator) {
    Set<Long> ids = new HashSet<>();
    long prevId = 0;

    for (int i = 0; i < 10_000; i++) {
      Snowflake snowflake = generator.next();

      Assertions.assertNotNull(snowflake);
      Assertions.assertTrue(
          snowflake.value() > prevId,
          String.format(
              "Generated id %d is not greater the previous " + "id %d on iteration %d",
              snowflake.value(), prevId, i));
      prevId = snowflake.value();

      Assertions.assertTrue(ids.add(prevId), "Found duplicate id " + prevId);
    }

    Assertions.assertEquals(10_000, ids.size());
  }

  @ParameterizedTest
  @MethodSource("provide_next_generators_success")
  public void next_generatorsInMultiThreadedEnvironment_success(SnowflakeGenerator generator) {
    List<Future<Snowflake>> futures = new ArrayList<>();
    Set<Long> results = new HashSet<>();
    try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
      for (int i = 0; i < 100; i++) {
        futures.add(executorService.submit(generator::next));
      }

      // Fetch all the results and make sure all ids are unique
      for (Future<Snowflake> future : futures) {
        Snowflake snowflake = future.get();
        Assertions.assertTrue(
            results.add(snowflake.value()),
            String.format("Duplicate id found %d", snowflake.value()));
      }
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private Stream<Arguments> provide_next_generators_success() {
    return Stream.of(
        Arguments.of(SnowflakeGenerator.createDefault()),
        Arguments.of(
            SnowflakeGenerator.create(
                SnowflakeConfiguration.builder()
                    .withTimestampBits(45)
                    .withWorkerBits(6)
                    .withSequenceBits(12)
                    .build(),
                GeneratorConfiguration.builder().withDataCenter(0L).withWorker(5L).build(),
                DefaultTime.getDefault(clock))));
  }

  @Test
  public void next_generateSequenceForSameTimestamp_success() {
    // Arrange
    Clock c = mock(Clock.class);
    when(c.millis()).thenReturn(EPOCH_MILLIS);
    int generateCount = 5;

    // Act
    List<Snowflake> snowflakes = new ArrayList<>();
    SnowflakeGenerator generator =
        SnowflakeGenerator.create(
            SnowflakeConfiguration.getDefault(),
            GeneratorConfiguration.builder().withDataCenter(0L).withWorker(5L).build(),
            DefaultTime.getDefault(c));
    for (int i = 0; i < generateCount; i++) {
      snowflakes.add(generator.next());
    }

    // Assert
    Assertions.assertEquals(generateCount, snowflakes.size());
    int seq = 0;
    for (Snowflake snowflake : snowflakes) {
      Assertions.assertEquals(EPOCH_MILLIS - Utils.DEFAULT_EPOCH, snowflake.getTimeStamp());
      Assertions.assertEquals(0, snowflake.getDataCenter());
      Assertions.assertEquals(5, snowflake.getWorker());
      Assertions.assertEquals(seq, snowflake.getSequence());
      seq++;
    }
  }
}
