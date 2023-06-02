package io.curiositycore.landlord.util.war.participants.combatstats;

import io.curiositycore.landlord.util.war.participants.combatstats.types.AttackerStatType;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Defines the combat statistics of a <code>Participant</code> on the attacking team.
 */
public class AttackerStats extends CombatStats {
    /**
     * A <code>HashMap</code> defining the attacker's specific combat statistics defined with <code>Long</code>
     * <code>Value</code>s and <code>AttackerStatType</code> <code>Key</code>s.
     */
    private HashMap<AttackerStatType, Double> attackerStatsMap = new HashMap<>();

    @Override
    public void constructSpecificTeamStats() {
        Arrays.stream(AttackerStatType.values()).toList().forEach(enumVal-> this.attackerStatsMap.put(enumVal, 0.0));
    }

    @Override
    public <T extends Enum<T>> Double getSpecificStat(T attackerStatType) {
        return this.attackerStatsMap.get((AttackerStatType) attackerStatType);
    }

    @Override
    public <T extends Enum<T>> void changeSpecificStat(T attackerStatType, Double changeValue) {
        double changedValue = this.attackerStatsMap.get(attackerStatType)+changeValue;
        this.attackerStatsMap.put((AttackerStatType) attackerStatType,changedValue);
    }
}
