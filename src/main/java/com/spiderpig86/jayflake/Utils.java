package com.spiderpig86.jayflake;

/** Helper methods for Jayflake. */
public class Utils {

  // Wed Jan 01 2020 08:00:00 GMT+0000
  public static final long DEFAULT_EPOCH = 1577865600000L;

  /**
   * Helper method to extract value given the mask length and offset from the least-significant bit.
   *
   * @param value value to fetch mask from.
   * @param maskLength length of the mask (e.g. in a 64-bit value, I want to fetch 12 bits).
   * @param maskOffset how much to shift the generated mask from maskLength to the left away from
   *     the least-significant bit.
   * @return extracted value using bit mask.
   */
  public static long getValueWithMask(
      final long value, final int maskLength, final int maskOffset) {
    long mask = ((1L << maskLength) - 1) << maskOffset;
    return (value & mask) >> maskOffset;
  }
}
