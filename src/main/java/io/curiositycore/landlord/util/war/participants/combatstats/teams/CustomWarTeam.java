package io.curiositycore.landlord.util.war.participants.combatstats.teams;

import io.curiositycore.landlord.util.war.participants.Attacker;
import io.curiositycore.landlord.util.war.participants.Defender;
import io.curiositycore.landlord.util.war.participants.Participant;
import io.curiositycore.landlord.util.war.participants.combatstats.CombatStats;
import io.curiositycore.landlord.util.war.participants.combatstats.teams.enums.TeamType;
import me.angeschossen.lands.api.land.ChunkCoordinate;
import me.angeschossen.lands.api.memberholder.MemberHolder;
import me.angeschossen.lands.api.war.War;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;

/**
 * Abstract used to define the generalisation of a team of <code>Participant</code> instances within a
 * <code>CustomWar</code>. Teams define these participants, along with overall statistics, <code>TeamType</code>,
 * and properties to do with the <code>Land</code> claim the team is a part of.
 */

public abstract class CustomWarTeam {
    /**
     * The name of the Team's <code>Land</code> claim.
     */
    protected final String teamName;
    /**
     * The <code>TeamType</code> of the team.
     */
    protected final TeamType teamType;
    /**
     * A <code>HashMap</code> with <code>Participant</code> instances <code>Value</code>s and <code>UUID</code>
     * <code>Key</code>s for the team.
     */
    protected final HashMap<UUID, Participant> participantMap;
    /**
     * A <code>Collection</code> of <code>ChunkCoordinate</code> instances, that correspond to the X and Y
     * coordinates of <code>Chunk</code>s within the Team's <code>Land</code> claim.
     */
    protected final Collection<ChunkCoordinate> chunkCoordinates;
    /**
     * The overall <code>CombatStats</code> for the entire team. This is a collection of all the statistics of
     * every <code>Participant</code> within the team.
     */
    protected final CombatStats teamStats;

    /**
     * Constructor that initialises the name, type of team, and identifying information about the <code>Land</code>
     * claim the team is a part of.
     * @param associatedLandsWar The <code>War</code> the team are participating in. <i>(Necessary to get the
     *                          information about the team's <code>Land</code> claim that is required for the
     *                           <code>CustomWar</code>)</i>.
     * @param chunkCoordinates The x and z coordinates of <code>Chunk</code>s within the team's <code>Land</code> claim.
     * @param warName The name of the <code>CustomWar</code>.
     */
    public CustomWarTeam(War associatedLandsWar, Collection<ChunkCoordinate> chunkCoordinates, String warName){
        this.teamType = constructTeamType();
        MemberHolder teamMemberHolder = this.teamType.getTeamMemberHolder(associatedLandsWar);
        Collection<UUID>  playerUIDCollection = teamMemberHolder.getTrustedPlayers();


        this.teamName = teamMemberHolder.getName();
        this.participantMap = constructTeamMap(playerUIDCollection, warName);
        this.chunkCoordinates = chunkCoordinates;
        this.teamStats = this.teamType.getNewCombatStats();

    }

    /**
     * Gets the name of the team.
     * @return The name of the team.
     */
    public String getTeamName() {
        return teamName;
    }
    /**
     * Gets the name type of team.
     * @return
     */
    public TeamType getTeamType() {
        return teamType;
    }
    /**
     * Gets the <code>participantMap</code> of the team.
     * @return The <code>participantMap</code> of the team.
     */
    public HashMap<UUID, Participant> getParticipantMap() {
        return participantMap;
    }

    /**
     * Gets a <code>Collection</code> of <code>ChunkCoordinate</code> instances for the team.
     * @return A <code>Collection</code> of <code>ChunkCoordinate</code> instances for the team.
     */
    public Collection<ChunkCoordinate> getChunkCoordinates() {
        return chunkCoordinates;
    }

    /**
     * Gets the overall <code>CombatStats</code> of the team.
     * @return The overall <code>CombatStats</code> of the team.
     */
    public CombatStats getTeamStats() {
        return teamStats;
    }

    /**
     * Constructs the <code>teamMap</code> for the team, utilizing the team's UUID <code>UUID Collection</code>.
     * @param participantUIDs A <code>Collection</code> of <code>UUID</code> instances that correspond to the
     *                        <code>Participant</code>s involved in the <code>CustomWar</code>.
     * @param warName The name of the <code>CustomWar</code>.
     * @return The <code>teamMap</code> for the team.
     */
    private HashMap<UUID,Participant> constructTeamMap(Collection<UUID> participantUIDs, String warName){
        HashMap<UUID,Participant> participantHashMap = new HashMap<>();

        participantUIDs.forEach(playerUID->{
            Map.Entry<UUID,Participant> participantEntry = participantConstructor(playerUID, warName);
            participantHashMap.put(participantEntry.getKey(),participantEntry.getValue());
        });
        return participantHashMap;
    }

    /**
     * Constructs each <code>Map.Entry</code> for the <code>teamMap</code> corresponding to a singular
     * <code>Participant</code>.
     * @param participantUUID <code>UUID</code> instances of a <code>Participant</code> of the <code>CustomWar</code>.
     * @param warName The name of the <code>CustomWar</code>.
     * @return <code>Map.Entry</code> for the <code>teamMap</code>.
     */
    private Map.Entry<UUID,Participant> participantConstructor(UUID participantUUID, String warName){
        Participant participant;
        OfflinePlayer participatingPlayer = Bukkit.getOfflinePlayer(participantUUID);

        if(this.teamType.equals(TeamType.ATTACKING_TEAM)){
            participant = new Attacker(participatingPlayer,warName);
            return new AbstractMap.SimpleEntry<>(participantUUID,participant);
        }

        participant = new Defender(participatingPlayer,warName);
        return new  AbstractMap.SimpleEntry<>(participantUUID,participant);
    }

    /**
     * Abstract method to define the generalisation of constructing the <code>TeamType</code> of the team.<br>
     * <i>(This is it's own method as a workaround to allowing multiple subclasses of this abstract class have the same
     * TeamType, for future additions like mercenaries.)</i>
     * @return The type this team falls under in terms of the <code>CustomWar</code> it is participating in.
     */
    protected abstract TeamType constructTeamType();
}
