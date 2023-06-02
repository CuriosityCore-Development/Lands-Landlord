package io.curiositycore.landlord.util.war.participants;

import io.curiositycore.landlord.util.war.participants.combatstats.AttackerStats;
import io.curiositycore.landlord.util.war.participants.combatstats.DefenderStats;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
/**
 * Defines a <code>Participant</code> on the attacking team within a <code>CustomWar</code>
 */
//TODO complete class
public class Attacker extends Participant{
    /**
     * Constructor that initializes the parent class fields.
     * @param participatingPlayer the <code>OfflinePlayer</code> instance of the attacking player participating.
     * @param customWarName the name of the <code>CustomWar</code> the attacking player is participating in.
     */
    public Attacker(OfflinePlayer participatingPlayer, String customWarName) {
        super(participatingPlayer,customWarName);

    }

    @Override
    protected void constructCombatStats() {
        this.combatStats = new AttackerStats();
    }

}
