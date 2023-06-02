package io.curiositycore.landlord.util.war.scoring;

/**
 * Enum defining the different conditions for scoring a point in a <code>CustomWar</code>
 */
public enum ScoreConditions {
    /**
     * This condition grants points over time if an area is controlled (For game modes such as "King Of The Hill")
     */
    AREA_CONTROL,
    /**
     * This condition grants points if a specific area is captured via a "Capture Point"
     */
    CAPTURE_POINT,
    /**
     * This condition grants points for every kill a <code>Participant</code> gets over another <code>Participant</code>.
     */
    KILLS

}
