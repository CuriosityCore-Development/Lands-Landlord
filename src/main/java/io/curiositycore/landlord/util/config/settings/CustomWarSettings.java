package io.curiositycore.landlord.util.config.settings;

import io.curiositycore.landlord.util.config.ConfigValueAddresses;
/**
 * Enum for defining the settings for the Custom War functionality of the <code>Plugin</code>
 */
public enum CustomWarSettings implements ConfigValueAddresses {
    /**
     * The <code>Integer</code> value used to determine how much influence is required to capture a controllable area.
     */
    AREA_INFLUENCE_REQUIREMENT("area_influence_requirement"),
    /**
     * The <code>Integer</code> value used to determine the control radius (max distance away from the block to count
     * towards the capture).
     */
    AREA_CAPTURE_RADIUS("area_capture_radius"),
    /**
     * Number of point scored when a <code>ControllableArea</code> is captured during a <code>CustomWar</code>.
     */
    POINTS_FROM_AREA_CAPTURE("points_scored_from_area_capture"),

    POINTS_REQUIRED_FOR_VICTORY("points_required_for_victory");

    /**
     * The address of the config-value's section in the config file, as a <code>String</code>.
     */
    private final String valueAddress;
    /**
     * The sub-address of the config-value, as a <code>String</code>.
     */
    private final String sectionAddress;
    CustomWarSettings(String valueAddress){
        this.valueAddress = valueAddress;
        this.sectionAddress = "custom_war";
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
