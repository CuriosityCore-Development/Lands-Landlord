package io.curiositycore.landlord.util.war.scoring;

/**
 * Enum defining the different conditions for scoring a point in a <code>CustomWar</code>
 */
public enum ScoreConditions {
    /**
     * Condition in which players can capture a flag to gain points, each team has its own camp within
     * their <code>Land</code> claim. To score points the flag must be taken back to the flag location
     * within their claim. You cannot use <code>/la spawn</code>, nor any teleport method, if you have
     * the flag in hand.
     */
    CAPTURE_THE_FLAG,
    /**
     * Condition in which a defending <code>Land</code> claim is seperated into areas which are capture
     * by the attacking team. Areas much be captured from the outside of the claim, inwards. If the central
     * area is taken, then the defenders lose. If the defenders can hold on until the end of the war, they win.
     */
    AREA_CONTROL,
    /**
     * Condition which works similarly to typical capture point <code>War</code> instances within the <code>Lands</code>
     * plugin. The difference is, capture flags initiate an area control fight, in which the area is taken, and points
     * earned, if the one team is able to fill an "influence" bar to full. To gain influence, players must have more
     * of their team within a designated <code>ControllableArea</code> than the enemy team.
     */
    CONQUEST;

}
