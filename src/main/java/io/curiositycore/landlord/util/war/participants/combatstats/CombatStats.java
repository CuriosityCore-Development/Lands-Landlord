package io.curiositycore.landlord.util.war.participants.combatstats;

import io.curiositycore.landlord.util.war.participants.combatstats.types.CombatStatType;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Abstract defining the generalisation of the combat statistics of a <code>Player</code> participating within a
 * <code>CustomWar</code>.
 */
public abstract class CombatStats {
    /**
     * A <code>HashMap</code> defining the participants current combat statistics defined with <code>Long</code>
     * <code>Value</code>s and <code>CombatStatType</code> <code>Key</code>s.
     */
    HashMap<CombatStatType, Double> statHashMap = new HashMap<>();

    /**
     * Constructor that initializes the <code>statHashMap</code> with all the currently defined
     * <code>CombatStatType</code>s.
     */
    public CombatStats(){
        Arrays.stream(CombatStatType.values()).toList().forEach(enumVal-> statHashMap.put(enumVal, 0.0));

    }

    /**
     * Gets the current value of the requested <code>CombatStatType</code>.
     * @param combatStatType The requested <code>CombatStatType</code>.
     * @return The value of the <code>CombatStatType</code>.
     */
    public Double getCombatStat(CombatStatType combatStatType){
        return statHashMap.get(combatStatType);
    }

    /**
     * Implements a change to the score of the requested <code>CombatStatType</code>. <br><i>(Is synchronized due
     * to the likelihood of concurrent calls of this method by the <code>WarEvents</code> class)</i>
     * @param combatStatType The requested <code>CombatStatType</code>.
     * @param statChange The value of the change to be implemented to the statistic.
     */
    public synchronized void statHashMapChange(CombatStatType combatStatType, Double statChange){
       this.statHashMap.put(combatStatType,this.
                        statHashMap.
                        get(combatStatType) + statChange);
    }

    /**
     * Abstract method for the generalisation of constructing statistics specific to each team type.
     */
    public abstract void constructSpecificTeamStats();
    //TODO I have no idea if this will work
    /**
     * Abstract method for the generalisation of a getter for team specific statistics.<br> <i>(Generics implemented for
     * future-proofing)</i>
     */
    public abstract <T extends Enum<T>> Double getSpecificStat(T enumValue);
    //TODO I have no idea if this will work either lol
    /**
     * Abstract method for the generalisation of changing team specific statistics.<br> <i>(Generics implemented for
     * future-proofing)</i>
     */
    public abstract <T extends Enum<T>> void changeSpecificStat(T enumValue,Double changeValue);
}
