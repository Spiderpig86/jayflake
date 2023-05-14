# ❄ Jayflake

A Java implementation similar to Twitter's now deprecated [Snowflake ID](https://blog.twitter.com/engineering/en_us/a/2010/announcing-snowflake) 
that is thread-safe. Note that this is only the library for 
generating the ids. It does not include a service that provides ids to calling backend services.

> Note: This project does not include a backend service for id generation.
 
## Usage

```java
// Default generator
SnowflakeGenerator generator = SnowflakeGenerator.getDefault();

// Generator with custom configurations
SnowflakeGenerator generator = SnowflakeConfiguration.builder()
      .withTimestampBits(41)
      .withDatacenterBits(8)
      .withWorkerBits(2)
      .withSequenceBits(12)
      .build(),
    GeneratorConfiguration.builder()
      .withDataCenter(1L)
      .withWorker(6L)
      .withOverflowStrategy(OverflowStrategy.SPIN_WAIT)
      .build(),
    new DefaultTime(Clock.systemDefaultZone(), Instant.now()));
```

## Installation

TBA

> Java 9 or above is required.

## How it works

Below is a diagram depicting the structure of a default Snowflake id.

- The first bit is unused to make ordering easier across both signed and unsigned values.
- The next 41 bits are the timestamp, or the number of ticks since the epoch. By default, this can store a range of 
  69 years from the epoch where the tick duration is 1 millisecond.
- The next 4 bits store an id indicating the data center of which the machine lives in.
- The next 6 bits store an id of the worker generating the id.
- The last 12 bits store a count representing the sequence to handle any collisions.

```
+------------+---------------------+-----------+---------+----------------------+
| 1 Bit      | 41 Bit              | 4 Bit     | 6 Bit   | 12 Bit               |
| Unused     | Timestamp           | Data Ctr  | Worker  | Sequence Number      |
+------------+---------------------+-----------+---------+----------------------+
```

| Unused bit | Timestamp                                       | Data center | Worker | Sequence        |
|------------|-------------------------------------------------|-------------|--------|-----------------|
| 0          | 00000000000 00000000001 00111111010 01010010010 | 0000        | 000101 | 000000001011    |

Based on the example above which shows id `21941452820491` in binary, we know the following:
- `5231250` ticks passed since the _epoch_.
- The data center is `0`.
- The worker is `5`.
- The sequence id is `11`.

## Customization

One of the strengths of this implementation is that you are not tied down to the default configuration. There are 3 
main types of classes that determine the generator's behavior.

### `SnowflakeConfiguration`

The structure of the Snowflake id's bits is controlled by the `SnowflakeConfiguration`, a centralized place that 
determines which bits are reserved for what functionality.

For example, if you think the data center bits are unnecessary and want to copy the original Snowflake id's format 
of a 41 bit timestamp, 10 bit worker id, and 12 bit sequence id, you can do the following:

```java
SnowflakeGenerator generator =
    SnowflakeGenerator.create(
        SnowflakeConfiguration.builder()
        .withTimestampBits(41)
        .withDatacenterBits(0)
        .withWorkerBits(10)
        .withSequenceBits(12)
        .build(),
    GeneratorConfiguration.builder()
        .withDataCenter(0L)
        .withWorker(5L)
        .withOverflowStrategy(OverflowStrategy.SLEEP_WITH_JITTER)
        .build(),
    DefaultTime.getDefault(c));
```

### `GeneratorConfiguration`

The `GeneratorConfiguration` houses all the settings used while generating the ids themselves, such as the data 
center, worker id, and overflow strategy.

```java
GeneratorConfiguration.builder()
    .withDataCenter(0L)
    .withWorker(5L)
    .withOverflowStrategy(OverflowStrategy.SLEEP_WITH_JITTER)
    .build()
```

### Classes extending `Time`

These classes are the source of truth of the `epoch` (similar to the UNIX epoch) and the length of each tick.
The `DefaultTime` class is provided out of the box which uses a tick duration of `1ms`.

You can increase the duration of the tick to whatever duration you want to extend how much time the id can span. 
_Keep in mind that this can increase the number of ids that are generated with the same timestamp which runs the 
risk of overflowing the `sequence` bits_.

Below is an example Time class which uses `1s` as the tick duration.

```java
public class OneSecondTime extends Time {

  private final long tickDurationMs;

  public OneSecondTime(@Nonnull final Clock clock, @Nonnull final Instant epoch) {
    super(clock, epoch);
    this.tickDurationMs = 1000L;
  }

  @Override
  public long getTickDurationMs() {
    return tickDurationMs;
  }
}
```

## Overflows

Snowflake ids are no strangers to overflow situations, especially if the bits are configured improperly. Even if 
they are, with high enough QPS, we may run out of numbers.
In the event this happens, you can set a specific `OverflowStrategy` for when values overflow.

- `SLEEP` - SnowflakeGenerator will sleep for a provided duration in milliseconds.
- `SLEEP_WITH_JITTER` - SnowflakeGenerator will sleep for a provided duration plus a random jitter in milliseconds.
- `SPIN_WAIT` - SnowflakeGenerator will provide a system hint to allow other threads to be scheduled and while two provided values are equal.
- `THROW_EXCEPTION` - SnowflakeGenerator will throw an exception when encountering an overflow.

Currently, this applies to the `sequence` bits. If the timestamp overflows, you have a much bigger problem on your 
hands.

## License

This project is licensed under the MIT license. Please see the [license](./LICENSE) for more details.
