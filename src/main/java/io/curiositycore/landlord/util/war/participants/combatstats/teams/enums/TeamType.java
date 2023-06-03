package io.curiositycore.landlord.util.war.participants.combatstats.teams.enums;

import io.curiositycore.landlord.util.war.participants.combatstats.AttackerStats;
import io.curiositycore.landlord.util.war.participants.combatstats.CombatStats;
import io.curiositycore.landlord.util.war.participants.combatstats.DefenderStats;
import me.angeschossen.lands.api.war.*;
import me.angeschossen.lands.api.memberholder.MemberHolder;


import java.util.function.Function;

/**
 * Enum for defining the different types of team that can exist within a <code>CustomWar</code>
 */
public enum TeamType {
    /**
     * The team that initiated the war.
     */
    ATTACKING_TEAM() {
        @Override
        public MemberHolder getTeamMemberHolder(War associatedWar) {
            return associatedWar.getAttacker();
        }

        @Override
        public CombatStats getNewCombatStats() {
            return new AttackerStats();
        }
    },
    /**
     * The team being attacked within the war.
     */
    DEFENDING_TEAM(){
        @Override
        public MemberHolder getTeamMemberHolder(War associatedWar) {
            return associatedWar.getDefender();
        }

        @Override
        public CombatStats getNewCombatStats() {
            return new DefenderStats();
        }
    },
    /**
     * The team is a group of mercenaries hired by either team in the war.<br> <i>(This is here for future-proofing
     * as eventually functionality for mercenaries is planned)</i>
     */
    MERCENARY_TEAM() {
        @Override
        public MemberHolder getTeamMemberHolder(War associatedWar) {
            return null;
        }

        @Override
        public CombatStats getNewCombatStats() {
            throw new UnsupportedOperationException("Mercenary teams are not supported yet");
        }
    };

    /**
     * Gets the <code>MemberHolder</code> for the <code>TeamType</code> for the associated <code>CustomWar</code>.
     * @param associatedWar The associated <code>CustomWar</code> instance to get the <code>MemberHolder</code>.
     * @return The <code>MemberHolder</code> for the <code>TeamType</code>.
     */
    public abstract MemberHolder getTeamMemberHolder(War associatedWar);

    /**
     * Abstract method, defined for each value, that returns a new instance of the value's corresponding
     * <code>CombatStats</code> subclass.
     * @return Instance of the value's corresponding<code>CombatStats</code> subclass.
     */
    public abstract CombatStats getNewCombatStats();


}
