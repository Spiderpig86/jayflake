package com.spiderpig86.snowflake;

import com.google.common.base.Preconditions;
import com.spiderpig86.snowflake.configuration.GeneratorConfiguration;
import com.spiderpig86.snowflake.configuration.SnowflakeConfiguration;
import com.spiderpig86.snowflake.model.Snowflake;
import com.spiderpig86.snowflake.time.DefaultTime;
import com.spiderpig86.snowflake.time.Time;

import javax.annotation.Nonnull;
import java.time.Instant;

import static com.spiderpig86.snowflake.Utils.getValueWithMask;

/**
 * A thread-safe class for generating Snowflake ids.
 */
public class SnowflakeGenerator {

    private final SnowflakeConfiguration snowflakeConfiguration;
    private final GeneratorConfiguration generatorConfiguration;
    private final Time time;

    private long previousTimestamp = -1;
    private long sequence = 0;


    private SnowflakeGenerator(@Nonnull final SnowflakeConfiguration snowflakeConfiguration,
                               @Nonnull final GeneratorConfiguration generatorConfiguration,
                               @Nonnull final Time time) {
        this.snowflakeConfiguration = Preconditions.checkNotNull(snowflakeConfiguration);
        this.generatorConfiguration = Preconditions.checkNotNull(generatorConfiguration);
        this.time = Preconditions.checkNotNull(time);

        validateConfigurations();
    }

    public static SnowflakeGenerator createDefault() {
        final Instant now = Instant.now();
        return new SnowflakeGenerator(SnowflakeConfiguration.getDefault(), GeneratorConfiguration.getDefault(now),
                new DefaultTime(now));
    }

    public static SnowflakeGenerator create(@Nonnull final SnowflakeConfiguration snowflakeConfiguration,
                                            @Nonnull final GeneratorConfiguration generatorConfiguration,
                                            @Nonnull final Time time) {
        return new SnowflakeGenerator(snowflakeConfiguration, generatorConfiguration, time);
    }

    public Snowflake next() {
        // Get current timestamp
        // Get sequence
        // Generate new id
        long timestamp = getValueWithMask(System.currentTimeMillis(), snowflakeConfiguration.getTimeStampBits(), 0);
        if (previousTimestamp == timestamp) {
            // Times are the same, increment the sequence
            sequence++;
        }

        return new Snowflake(timestamp, generatorConfiguration.getDataCenter(), generatorConfiguration.getWorker(),
                sequence, snowflakeConfiguration);
    }

    private void validateConfigurations() {
        // Ensure that the provided GeneratorConfiguration values are in range
        Preconditions.checkArgument(generatorConfiguration.getDataCenter() >= 0 && generatorConfiguration.getDataCenter() <= snowflakeConfiguration.getMaxDataCenter(), "Provided data center value is out of bounds.");
        Preconditions.checkArgument(generatorConfiguration.getWorker() >= 0 && generatorConfiguration.getWorker() <= snowflakeConfiguration.getMaxWorker(), "Provided worker value is out of bounds.");
    }
}
