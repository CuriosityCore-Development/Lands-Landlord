package io.curiositycore.landlord.util.maths;

/**
 * Enum that holds  ticks into units of contextualised Time Units.
 */
public enum TimeConverter {
    /**
     * The base time of a Minecraft Tick. <i>(Present for completeness and future proofing)</i>
     */
    TICK(1),
    /**
     * The amount of Minecraft Ticks within 1 Second.
     */
    SECOND(20),
    /**
     * The amount of Minecraft Ticks within 1 Minute.
     */
    MINUTE(20 * 60),
    /**
     * The amount of Minecraft Ticks within 1 Hour.
     */
    HOUR(20 * 60 * 60),
    /**
     * The amount of Minecraft Ticks within 1 Day.
     */
    DAY(20 * 60 * 60 * 24);
    /**
     * The number of Minecraft Ticks. There are, ideally, 20 Minecraft Ticks per second.
     */
    private final int ticks;

    /**
     * Constructs a new time unit with the specified number of Minecraft Ticks.
     * @param ticks The number of Minecraft ticks for the specified Time Unit.
     */
    TimeConverter(int ticks) {
        this.ticks = ticks;
    }

    /**
     * Converts the specified value of the Time Unit into Minecraft Ticks.
     * @param value The value of the specified Time Unit to convert to Minecraft Ticks.
     * @return The equivalent number of Minecraft Ticks for the value of the specified Time Unit.
     */
    public int toTicks(int value) {
        return value * ticks;
    }
}
