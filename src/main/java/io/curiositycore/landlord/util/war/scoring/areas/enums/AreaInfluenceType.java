package io.curiositycore.landlord.util.war.scoring.areas.enums;

import io.curiositycore.landlord.util.war.participants.combatstats.teams.enums.TeamType;
import org.jetbrains.annotations.Nullable;

/**
 * Enum for defining the different types of influence required capture that can occur within a
 * <code>CustomWar</code>.<br>
 * <i>(This is here for future-proofing, in-case capture mechanics for mercenaries are to be implemented in future)</i>
 */
public enum AreaInfluenceType {
    /**
     * Influence for the attacking team.
     */
    ATTACKER_INFLUENCE(TeamType.ATTACKING_TEAM),
    /**
     * Influence for the defending team.
     */
    DEFENDER_INFLUENCE(TeamType.DEFENDING_TEAM);
    /**
     * The <code>TeamType</code> that is the source of the influence game.<br> <i>(Can be Nullable as some types of
     * influence gain might not be caused by <code>Participant</code> instances that are part of a team)</i>
     */
    private final TeamType teamType;

    AreaInfluenceType(@Nullable TeamType teamType) {
        this.teamType = teamType;
    }

    /**
     * Getter for the <code>TeamType</code> associated with the type of influence gain.
     * @return
     */
    public TeamType getTeamType(){
        return teamType;
    }
}
