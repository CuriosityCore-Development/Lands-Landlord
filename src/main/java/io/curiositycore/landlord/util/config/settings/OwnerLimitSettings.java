package io.curiositycore.landlord.util.config.settings;

import io.curiositycore.landlord.util.config.ConfigValueAddresses;
/**
 * Enum for defining the settings for the Activity Scan functionality of the <code>Plugin</code>
 */
public enum OwnerLimitSettings implements ConfigValueAddresses {
    /**
     * The <code>boolean</code> value used to determine if the functionality of land ownership limitation is enabled or
     * not.
     */
    OWNER_LIMIT_ENABLED("enabled"),

    /**
     * The <code>int</code> value used to determine the number of <code>Land</code> claims a <code>Player</code> cam
     * own.
     */
    OWNER_LIMIT("land_ownership_limit");

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
    OwnerLimitSettings(String valueAddress){
        this.valueAddress = valueAddress;
        this.sectionAddress = "owner_limit";
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
