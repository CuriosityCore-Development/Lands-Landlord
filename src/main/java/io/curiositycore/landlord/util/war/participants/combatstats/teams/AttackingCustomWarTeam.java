package io.curiositycore.landlord.util.war.participants.combatstats.teams;

import io.curiositycore.landlord.util.war.participants.combatstats.teams.enums.TeamType;
import me.angeschossen.lands.api.land.ChunkCoordinate;
import me.angeschossen.lands.api.war.War;

import java.util.Collection;

/**
 * The <code>Team</code> that initialised the attack against the defender in this <code>CustomWar</code>.
 */
public class AttackingCustomWarTeam extends CustomWarTeam {
    /**
     * Constructor that initialises the super of its parent class.
     * @param associatedLandsWar The <code>War</code> the team are participating in. <i>(Necessary to get the
     *                          information about the team's <code>Land</code> claim that is required for the
     *                           <code>CustomWar</code>)</i>.
     * @param chunkCoordinates The x and z coordinates of <code>Chunk</code>s within the team's <code>Land</code> claim.
     * @param warName The name of the <code>CustomWar</code>.
     */
    public AttackingCustomWarTeam(War associatedLandsWar, Collection<ChunkCoordinate> chunkCoordinates, String warName) {
        super(associatedLandsWar, chunkCoordinates, warName);
    }

    @Override
    protected TeamType constructTeamType() {
        return TeamType.ATTACKING_TEAM;
    }
}
