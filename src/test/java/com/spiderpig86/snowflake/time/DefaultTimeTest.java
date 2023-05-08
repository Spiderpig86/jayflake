package com.spiderpig86.snowflake.time;

import static com.spiderpig86.snowflake.Utils.DEFAULT_EPOCH;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultTimeTest {
  private static final Instant NOW = Instant.now();
  private static final Instant BEFORE = NOW.minus(5, ChronoUnit.MINUTES);

  @Mock private Clock clock;

  @Test
  public void testDefaultTime() {
    // Arrange
    when(clock.millis()).thenReturn(NOW.toEpochMilli());

    // Act
    final Time defaultTime = new DefaultTime(clock, BEFORE);

    // Assert
    Assertions.assertEquals(BEFORE, defaultTime.getEpoch());
    Assertions.assertEquals(300000L, defaultTime.getTick());
    Assertions.assertEquals(1000L, defaultTime.getTickDurationMs());
  }

  @Test
  public void testDefaultInstance() {
    // Arrange
    when(clock.millis()).thenReturn(NOW.toEpochMilli());

    // Act
    final Time defaultTime = DefaultTime.getDefault(clock);

    // Assert
    Assertions.assertEquals(Instant.ofEpochMilli(DEFAULT_EPOCH), defaultTime.getEpoch());
    Assertions.assertEquals(NOW.minusMillis(DEFAULT_EPOCH).toEpochMilli(), defaultTime.getTick());
    Assertions.assertEquals(1000L, defaultTime.getTickDurationMs());
  }
}
