package io.curiositycore.landlord.util.config.enums;

import io.curiositycore.landlord.util.config.ConfigValueAddresses;

/**
 * Enum for defining the various addresses of values within the configuration file of the <code>Plugin</code>.
 */
public enum ActivityScanSettings implements ConfigValueAddresses {
    /**
     * The <code>boolean</code> value used to determine if the functionality of activity scans is enabled or not.
     */
    ACTIVITY_ENABLED("enabled"),
    /**
     * The value used to determine the range in which activity is scanned. <i>(in days)</i>
     */
    ACTIVITY_SCAN_RANGE("activity_range"),
    /**
     * The value used to determine the time period between automatic scans. <i>(in hours)</i>
     */
    ACTIVITY_SCAN_PERIOD("scan_period"),
    /**
     * The value used to determine the time delay before the first scan after the Plugin has loaded.
     * <i>(in minutes)</i>
     */
    ACTIVITY_SCAN_DELAY("delay_on_enable"),
    /**
     * The value used to determine activity time needed to not be considered "inactive".
     * <i>(in minutes)</i>
     */
    ACTIVITY_REQUIREMENT("activity_requirement"),

    /**
     * The value used to determine what time to add when repeat sessions are detected <i>(Usually caused by restarts
     * and crashes)</i>
     *
     */
    ACTIVITY_DEFAULT_SESSION_TIME("default_session_time");
    /**
     * The address of the config-value's section in the config file, as a <code>String</code>.
     */
    private final String valueAddress;
    /**
     * The sub-address of the config-value, as a <code>String</code>.
     */
    private final String sectionAddress;

    /**
     * Constructor for the config-value's address components.
     * @param valueAddress The sub-address of the config-value, as a <code>String</code>.
     */
    ActivityScanSettings(String valueAddress){
        this.valueAddress = valueAddress;
        this.sectionAddress = "activity_scan";
    }

    @Override
    public String getSectionAddress() {
        return sectionAddress;
    }

    @Override
    public String getValueAddress() {
        return valueAddress;
    }


}
