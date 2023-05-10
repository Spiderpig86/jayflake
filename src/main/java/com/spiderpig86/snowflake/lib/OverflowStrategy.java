package com.spiderpig86.snowflake.lib;

/**
 * Overflow strategy for {@link com.spiderpig86.snowflake.SnowflakeGenerator}. This is mainly used
 * for the sequence portion of the id, but can be extended for other fields if needed.
 */
public enum OverflowStrategy {
  SLEEP,
  SLEEP_WITH_JITTER,
  SPIN_WAIT,
  THROW_EXCEPTION
}
