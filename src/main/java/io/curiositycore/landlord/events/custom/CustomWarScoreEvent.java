package io.curiositycore.landlord.events.custom;

import io.curiositycore.landlord.util.war.participants.TeamType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
/**
 * Represents an <code>Event</code> that is triggered when points have been scored within a <code>CustomWar</code>.
 */
public class CustomWarScoreEvent extends Event {
    /**
     * The list of <code>EventHandler</code>s for this <code>Event</code>.
     */
    private static final HandlerList handlers = new HandlerList();
    /**
     * The name of the <code>CustomWar</code> in which points have been scored.
     */
    private String areaWarName;

    /**
     * The team that scored the points.
     */
    private TeamType teamThatScored;

    /**
     * Constructor that initializes the name of the war along with key information on the points scored.
     *
     * @param areaWarName The name of the <code>CustomWar</code> in which points have been scored.
     * @param teamThatScored The team that captured the <code>ControllableArea</code>
     */
    public CustomWarScoreEvent(String areaWarName, TeamType teamThatScored){
        this.areaWarName = areaWarName;
        this.teamThatScored = teamThatScored;

    }

    /**
     * Gets the name of the <code>CustomWar</code> in which points have been scored.
     * @return The <code>CustomWar</code>'s name.
     */
    public String getAreaWarName(){
        return this.areaWarName;
    }

    /**
     * Gets the team that scored points.
     * @return The scoring team.
     */
    public TeamType getTeamThatScored(){
        return this.teamThatScored;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return this.handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
