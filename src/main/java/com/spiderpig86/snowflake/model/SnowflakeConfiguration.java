package com.spiderpig86.snowflake.model;

import lombok.ToString;

/**
 * Configuration for the Snowflake id itself.
 */
@ToString
public class SnowflakeConfiguration {
    /* Default bit configuration */
    private static final int BIT_LENGTH = 63; // 64 bits, but sign bit is ignored
    private static final int DEFAULT_TIMESTAMP_BITS = 41;
    private static final int DEFAULT_DATA_CENTER_BITS = 4;
    private static final int DEFAULT_WORKER_BITS = 6;
    private static final int DEFAULT_SEQUENCE_BITS = 12;

    private final int timeStampBits;
    private final int dataCenterBits;
    private final int workerBits;
    private final int sequenceBits;

    protected SnowflakeConfiguration(final int timeStampBits, final int dataCenterBits, final int workerBits,
                                     final int sequenceBits) {
        this.timeStampBits = timeStampBits;
        this.dataCenterBits = dataCenterBits;
        this.workerBits = workerBits;
        this.sequenceBits = sequenceBits;
    }

    private static class Builder {
        int timeStampBits = DEFAULT_TIMESTAMP_BITS;
        int dataCenterBits = DEFAULT_DATA_CENTER_BITS;
        int workerBits = DEFAULT_WORKER_BITS;
        int sequenceBits = DEFAULT_SEQUENCE_BITS;

        public Builder withTimeStampBits(final int timeStampBits) {
            this.timeStampBits = timeStampBits;
            return this;
        }

        public Builder withDataCenterBits(final int dataCenterBits) {
            this.dataCenterBits = dataCenterBits;
            return this;
        }

        public Builder withWorkerBits(int workerBits) {
            this.workerBits = workerBits;
            return this;
        }

        public Builder withSequenceBits(int sequenceBits) {
            this.sequenceBits = sequenceBits;
            return this;
        }

        public SnowflakeConfiguration build() {
            validate();
            return new SnowflakeConfiguration(timeStampBits, dataCenterBits, workerBits, sequenceBits);
        }

        private void validate() {
            if (timeStampBits < 0 || timeStampBits >= BIT_LENGTH) {
                throw new IllegalArgumentException("Given timeStampBits is invalid");
            }
            if (dataCenterBits < 0 || dataCenterBits >= BIT_LENGTH) {
                throw new IllegalArgumentException("Given dataCenterBits is invalid");
            }
            if (workerBits < 0 || workerBits >= BIT_LENGTH) {
                throw new IllegalArgumentException("Given workerBits is invalid");
            }
            if (sequenceBits < 0 || sequenceBits >= BIT_LENGTH) {
                throw new IllegalArgumentException("Given sequenceBits is invalid");
            }

            if (timeStampBits + dataCenterBits + workerBits + sequenceBits != BIT_LENGTH) {
                throw new IllegalArgumentException(String.format("Sum of bit lengths does not equal %d", BIT_LENGTH));
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
