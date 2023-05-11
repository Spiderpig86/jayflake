package com.spiderpig86.jayflake.time;

import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TimeTest {

  private final Instant instant = LocalDateTime.of(2020, 2, 2, 2, 2, 2).toInstant(ZoneOffset.UTC);
  @Mock private Clock clock;

  @BeforeEach
  public void setup() {
    when(clock.millis())
        .thenReturn(
            LocalDateTime.of(2020, 2, 2, 2, 2, 30).toInstant(ZoneOffset.UTC).toEpochMilli());
  }

  @Test
  public void getTick_success() {
    // Arrange
    CustomTime customTime = new CustomTime(clock, instant);

    // Act & Assert
    Assertions.assertEquals(2000, customTime.getTickDurationMs());
    Assertions.assertEquals(14, customTime.getTick());
  }

  class CustomTime extends Time {
    CustomTime(Clock clock, Instant epoch) {
      super(clock, epoch);
    }

    @Override
    public long getTickDurationMs() {
      return 2000L;
    }
  }
}
