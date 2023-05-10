package com.spiderpig86.jayflake.lib;

/**
 * Overflow strategy for {@link com.spiderpig86.jayflake.SnowflakeGenerator}. This is mainly used
 * for the sequence portion of the id, but can be extended for other fields if needed.
 */
public enum OverflowStrategy {
  /** SnowflakeGenerator will sleep for a provided duration in milliseconds. */
  SLEEP,
  /** SnowflakeGenerator will sleep for a provided duration plus a random jitter in milliseconds. */
  SLEEP_WITH_JITTER,
  /**
   * SnowflakeGenerator will provide a system hint to allow other threads to be scheduled and while
   * two provided values are equal.
   */
  SPIN_WAIT,
  /** SnowflakeGenerator will throw an exception when encountering an overflow. */
  THROW_EXCEPTION
}
