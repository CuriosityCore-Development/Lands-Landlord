package io.curiositycore.landlord.util.war.participants.combatstats.types;
/**
 * Enum defining the general Combat Statistic Types for participants.
 */
public enum CombatStatType {
    /**
     * The amount of damage taken during the war.
     */
    DAMAGE_TAKEN,
    /**
     * The amount of damage dealt, to other participants, during the war.
     */
    DAMAGE_DEALT,
    /**
     * The amount of <code>Player</code>s killed, by the participant, during the war.
     */
    KILLS,
    /**
     * The amount of times the participant of a war has been killed.
     */
    DEATHS,
    /**
     * How many TNT blocks the participant of a war has placed.
     */
    TNT_PLACED,
    /**
     * How long the participant of a war has been online for during the war.
     */
    TIME_IN_WAR;
}
