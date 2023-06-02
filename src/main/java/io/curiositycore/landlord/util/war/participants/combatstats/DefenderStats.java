package io.curiositycore.landlord.util.war.participants.combatstats;

import io.curiositycore.landlord.util.war.participants.combatstats.types.CombatStatType;
import io.curiositycore.landlord.util.war.participants.combatstats.types.DefenderStatType;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Defines the combat statistics of a <code>Participant</code> on the defending team.
 */
public class DefenderStats extends CombatStats {
    /**
     * A <code>HashMap</code> defining the defender's current combat statistics defined with <code>Long</code>
     * <code>Value</code>s and <code>DefenderStatType</code> <code>Key</code>s.
     */
    private HashMap<DefenderStatType, Double> defenderStatsMap = new HashMap<>();;
    @Override
    public void constructSpecificTeamStats() {
        Arrays.stream(CombatStatType.values()).toList().forEach(enumVal-> statHashMap.put(enumVal, 0.0));
    }

    @Override
    public <T extends Enum<T>> Double getSpecificStat(T defenderStatType) {
        return this.defenderStatsMap.get((DefenderStatType) defenderStatType);

    }

    @Override
    public <T extends Enum<T>> void changeSpecificStat(T defenderStatType, Double changeValue) {
        double changedValue = this.defenderStatsMap.get(defenderStatType)+changeValue;
        this.defenderStatsMap.put((DefenderStatType) defenderStatType,changedValue);
    }

}
