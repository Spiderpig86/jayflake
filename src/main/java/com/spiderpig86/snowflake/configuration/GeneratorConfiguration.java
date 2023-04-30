package com.spiderpig86.snowflake.configuration;

import com.google.common.base.Preconditions;
import java.time.Instant;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GeneratorConfiguration {

  private final Instant epoch;
  private final long dataCenter;
  private final long worker;

  private GeneratorConfiguration(
      @Nonnull final Instant epoch, final long dataCenter, final long worker) {
    this.epoch = Preconditions.checkNotNull(epoch);
    this.dataCenter = dataCenter;
    this.worker = worker;
  }

  public static class Builder {
    Instant epoch;
    Long dataCenter;
    Long worker;

    public Builder withEpoch(@Nonnull final Instant epoch) {
      this.epoch = epoch;
      return this;
    }

    public Builder withDataCenter(@Nonnull final Long dataCenter) {
      this.dataCenter = dataCenter;
      return this;
    }

    public Builder withWorker(@Nonnull final Long worker) {
      this.worker = worker;
      return this;
    }

    public GeneratorConfiguration build() {
      validate();
      return new GeneratorConfiguration(this.epoch, this.dataCenter, this.worker);
    }

    private void validate() {
      Preconditions.checkNotNull(epoch, "Epoch cannot be null for GeneratorConfiguration");
      Preconditions.checkNotNull(
          dataCenter, "Data center cannot be null for GeneratorConfiguration");
      Preconditions.checkNotNull(worker, "Worker cannot be null for GeneratorConfiguration");

      Preconditions.checkArgument(dataCenter >= 0, "Data center must be non-negative");
      Preconditions.checkArgument(worker >= 0, "Worker must be non-negative.");
    }
  }

  public static GeneratorConfiguration.Builder builder() {
    return new Builder();
  }

  public static GeneratorConfiguration getDefault(@Nonnull final Instant epoch) {
    Preconditions.checkNotNull(epoch);
    return new GeneratorConfiguration.Builder()
        .withEpoch(epoch)
        .withDataCenter(0L)
        .withWorker(0L)
        .build();
  }
}
