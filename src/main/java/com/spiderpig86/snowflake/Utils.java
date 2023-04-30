package com.spiderpig86.snowflake;

public class Utils {
  public static long getValueWithMask(
      final long value, final int maskLength, final int maskOffset) {
    long mask = ((1L << maskLength) - 1) << maskOffset;
    return (value & mask) >> maskOffset;
  }
}
