package io.curiositycore.landlord.util.war.participants;

/**
 * Enum for defining the different types of team that can exist within a <code>CustomWar</code>
 */
public enum TeamType {
    /**
     * The team that initiated the war.
     */
    ATTACKING_TEAM,
    /**
     * The team being attacked within the war.
     */
    DEFENDING_TEAM,
    /**
     * The team is a group of mercenaries hired by either team in the war.<br> <i>(This is here for future-proofing
     * as eventually functionality for mercenaries is planned)</i>
     */
    MERCENARY_TEAM;
}
