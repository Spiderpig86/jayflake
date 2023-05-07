package com.spiderpig86.snowflake.time;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.when;

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
        final DefaultTime defaultTime = new DefaultTime(clock, BEFORE);

        // Assert
        Assertions.assertEquals(BEFORE, defaultTime.getEpoch());
        Assertions.assertEquals(300000L, defaultTime.getTick());
        Assertions.assertEquals(1000L, defaultTime.getTickDurationMs());
    }
}
