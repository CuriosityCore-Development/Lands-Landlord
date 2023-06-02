package io.curiositycore.landlord.util.war.participants;

import io.curiositycore.landlord.util.war.CustomWar;
import io.curiositycore.landlord.util.war.participants.combatstats.CombatStats;
import org.bukkit.OfflinePlayer;

/**
 * Abstract to define the generalisation of a participating <code>Player</code> within a <code>CustomWar</code>.
 */
public abstract class Participant {
    /**
     * The name of the participating <code>Player</code>
     */
    protected final String name;
    /**
     * The name of the <code>CustomWar</code> the participant is in.
     */
    protected final String customWarName;
    /**
     * The <code>OfflinePlayer</code> instance of the player participating within the War.
     */

    protected final OfflinePlayer player;

    /**
     * The statistics of the Participant for the War.
     */
    protected CombatStats combatStats;


    /**
     * Constructor that initializes the defining fields of the Participant.
     * @param participatingPlayer The <code>OfflinePlayer</code> instance of the player participating in the war.
     */
    public Participant(OfflinePlayer participatingPlayer, String customWarName){
        this.name = participatingPlayer.getName();
        this.player = participatingPlayer;
        this.customWarName = customWarName;
        constructCombatStats();
    }

    /**
     * Gets the name of the Participant's <code>OfflinePlayer</code> instance.
     * @return The name of the Participant.
     */
    public String getName() {
        return name;
    }
    /**
     * Gets the Participant's <code>OfflinePlayer</code> instance.
     * @return The Participant's <code>OfflinePlayer</code> instance.
     */
    public OfflinePlayer getPlayer() {
        return player;
    }

    /**
     * Abstract method defining the generalisation of the constructor of the Participant's <code>CombatStats</code>
     */
    protected abstract void constructCombatStats();

    /**
     * Gets the <code>CombatStats</code> of the participating player.
     * @return The <code>CombatStats</code> of the participating player.
     */
    public CombatStats getCombatStats(){
        return this.combatStats;
    }

    /**
     * Gets the name of the <code>CustomWar</code> this participant belongs to.
     * @return The name of the participant's <code>CustomWar</code>.
     */
    public String getCustomWarName(){
        return customWarName;
    }

}
