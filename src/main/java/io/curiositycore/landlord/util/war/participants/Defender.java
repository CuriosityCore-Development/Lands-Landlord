package io.curiositycore.landlord.util.war.participants;

import io.curiositycore.landlord.util.war.participants.combatstats.AttackerStats;
import io.curiositycore.landlord.util.war.participants.combatstats.DefenderStats;
import io.curiositycore.landlord.util.war.participants.combatstats.types.AttackerStatType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Defines a <code>Participant</code> on the defending team within a <code>CustomWar</code>
 */
//TODO complete class
public class Defender extends Participant{

    /**
     * Constructor that initializes the parent class fields.
     * @param participatingPlayer the <code>OfflinePlayer</code> instance of the defending player participating.
     * @param customWarName the name of the <code>CustomWar</code> the defending player is participating in.
     */
    public Defender(OfflinePlayer participatingPlayer, String customWarName) {
        super(participatingPlayer, customWarName);
    }

    @Override
    protected void constructCombatStats() {
        this.combatStats = new DefenderStats();
    }
}
