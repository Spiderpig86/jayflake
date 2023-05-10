package com.spiderpig86.jayflake;

public class Utils {

  // Wed Jan 01 2020 08:00:00 GMT+0000
  public static final long DEFAULT_EPOCH = 1577865600000L;

  public static long getValueWithMask(
      final long value, final int maskLength, final int maskOffset) {
    long mask = ((1L << maskLength) - 1) << maskOffset;
    return (value & mask) >> maskOffset;
  }
}
