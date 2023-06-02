package io.curiositycore.landlord.util.war;

import io.curiositycore.landlord.util.messages.MessageSender;
import io.curiositycore.landlord.util.war.participants.Attacker;
import io.curiositycore.landlord.util.war.participants.Defender;
import io.curiositycore.landlord.util.war.participants.Participant;
import io.curiositycore.landlord.util.war.participants.TeamType;
import io.curiositycore.landlord.util.war.participants.combatstats.AttackerStats;
import io.curiositycore.landlord.util.war.participants.combatstats.DefenderStats;
import io.curiositycore.landlord.util.war.participants.combatstats.types.AttackerStatType;
import me.angeschossen.lands.api.land.ChunkCoordinate;
import me.angeschossen.lands.api.war.War;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Defines the team compositions and statistics for a Custom War occurring on the server.
 */
public class CustomWar {
    // TODO Add constructors for the attacker and defender Names
    /**
     * The name of the <code>Land</code> attacking.
     */
    private String attackerName;
    /**
     * The name of the <code>Land</code> being attacked.
     */
    private String defenderName;
    /**
     * A <code>HashMap</code> with <code>OfflinePlayer</code> instance <code>Value</code>s and <code>Participant</code>
     * <code>Key</code>s for the attacking team.
     */
    private HashMap<UUID,Participant> attackingPlayers;

    /**
     * A <code>HashMap</code> with <code>OfflinePlayer</code> instance <code>Value</code>s and <code>Participant</code>
     * <code>Key</code>s for the defending team.
     */
    private HashMap<UUID,Participant> defendingPlayers;

    /**
     * The overall <code>AttackerStats</code> instance of the <code>CustomWar</code>.<br><i>(These stats are seperated
     * into attacker and defender fields as there are some stats that are only applicable to attackers / defenders, such
     * as "Amount of cap blocks placed.)</i>
     */
    private final AttackerStats attackerStats;

    /**
     * The overall <code>DefenderStats</code> instance of the <code>CustomWar</code>.<br><i>(These stats are seperated
     * into attacker and defender fields as there are some stats that are only applicable to attackers / defenders, such
     * as "Amount of cap blocks placed.)</i>
     */
    private final DefenderStats defenderStats;

    private final Collection<ChunkCoordinate> attackerChunks;
    private final Collection<ChunkCoordinate> defenderChunks;

    private final War associatedLandsWar;
    private final MessageSender participantMessageSender;



    private Audience participantAudience;
    private HashMap<TeamType, Integer> teamScoreMap = new HashMap<>();
    private boolean isEnded = false;

    /**
     * Constructor that initializes an <code>ArrayList</code> of <code>Player</code> instances, in
     * addition to <code>PlayerCombatStats</code> for both the attacking and defending teams.
     * @param attackerUIDs A <code>Collection</code> of <code>UUID</code> instances for the attacking team.
     * @param defenderUIDs A <code>Collection</code> of <code>UUID</code> instances for the defending team.
     */
    public CustomWar(War associatedLandsWar,Collection<UUID> attackerUIDs, Collection<UUID> defenderUIDs,
                     Collection<ChunkCoordinate> attackerChunks, Collection<ChunkCoordinate> defenderChunks){
        this.attackerName = associatedLandsWar.getAttacker().getName();
        this.defenderName = associatedLandsWar.getDefender().getName();
        this.attackingPlayers = getTeamMap(attackerUIDs,TeamType.ATTACKING_TEAM);
        this.defendingPlayers = getTeamMap(defenderUIDs,TeamType.DEFENDING_TEAM);
        this.participantAudience = constructAudienceForWar(attackerUIDs,defenderUIDs);
        this.participantMessageSender = new MessageSender(this.participantAudience);
        this.attackerStats = new AttackerStats();
        this.defenderStats = new DefenderStats();
        this.attackerChunks = attackerChunks;
        this.defenderChunks = defenderChunks;
        this.associatedLandsWar = associatedLandsWar;
        constructInitialScoreMap();

    }

    /**
     * Getter for the <code>ArrayList</code> of <code>Player</code> instances for the attacking team.
     * @return The attacking team <code>Player</code> <code>ArrayList</code>.
     */
    public HashMap<UUID,Participant> getAttackingPlayers() {
        return attackingPlayers;
    }

    /**
     * Getter for the <code>ArrayList</code> of <code>Player</code> instances for the defending team.
     * @return The defending team <code>Player</code> <code>ArrayList</code>.
     */
    public HashMap<UUID,Participant> getDefendingPlayers() {
        return defendingPlayers;
    }

    /**
     * Getter for the <code>AttackerStats</code> instance of the <code>CustomWar</code>.
     * @return The <code>AttackerStats</code> instance for the War.
     */
    public AttackerStats getAttackerStats() {
        return attackerStats;
    }

    /**
     * Getter for the <code>DefenderStats</code> instance of the <code>CustomWar</code>.
     * @return The War's <code>DefenderStats</code> instance.
     */
    public DefenderStats getDefenderStats() {
        return defenderStats;
    }

    /**
     * Gets the name of the Custom War, as a <code>String</code>.
     * @return The name of the Custom War.
     */
    public String getWarName(){
        return attackerName+" vs "+ defenderName;
    }

    /**
     * Gets the name of the attacking team's <code>Land</code>.
     * @return The name of the attacking team's <code>Land</code>.
     */
    public String getAttackerName(){
        return this.attackerName;
    }

    /**
     * Gets the name of the defending team's <code>Land</code>.
     * @return The name of the defending team's <code>Land</code>.
     */
    public String getDefenderName(){
        return this.defenderName;
    }

    /**
     * Gets the chunks belonging to the attacking <code>Land</code>.
     * @return The chunks belonging to the attacking <code>Land</code>.
     */
    public Collection<ChunkCoordinate> getAttackerChunks() {
        return attackerChunks;
    }

    /**
     * Gets the chunks belonging to the defending <code>Land</code>.
     * @return The chunks belonging to the defending <code>Land</code>.
     */
    public Collection<ChunkCoordinate> getDefenderChunks() {
        return defenderChunks;
    }

    /**
     * Sets the score for a team that has just earned points.
     * @param scoringTeam The <code>TeamType</code> that scored points
     * @param newScore The amount of points the team has scored.
     */
    public synchronized void setTeamScoreMap(TeamType scoringTeam, int newScore){
        this.teamScoreMap.put(scoringTeam,newScore);
    }

    /**
     * Gets the <code>HashMap</code> containing the scores for each <code>TeamType</code> within this
     * <code>CustomWar</code>.
     * @return The <code>HashMap</code> containing the scores for each team in the war.
     */
    public synchronized HashMap<TeamType,Integer> getTeamScoreMap(){
        return this.teamScoreMap;
    }

    /**
     * Gets the <code>War</code> associated with this custom war instance.
     * @return The associated <code>War</code>.
     */
    public War getAssociatedLandsWar(){
        return this.associatedLandsWar;
    }

    /**
     * Gets the <code>MessageSender</code> for the applicable <code>Participant</code>s of the war.
     * @return The <code>MessageSender</code> for the applicable <code>Participant</code>s.
     */
    public MessageSender getParticipantMessageSender(){
        return this.participantMessageSender;
    }

    /**
     * Gets the <code>Audience</code> of <code>Participant</code> instances involved in this war.
     * @return The <code>Audience</code> of <code>Participant</code> instances.
     */
    public Audience getParticipantAudience() {
        return participantAudience;
    }

    public boolean getisEnded(){
        return this.isEnded;
    }

    public void setIsEnded(boolean setting){
        this.isEnded = setting;
    }

    /**
     * Creates a <code>HashMap</code> of <code>Participant</code> instances from a <code>Collection</code> of
     * <code>UUID</code> instances that correspond to a team within the War.
     * @param participantUIDs <code>Collection</code> of <code>UUID</code> instances for a team within the War.
     * @return A <code>HashMap</code> of <code>Participant</code></code> instances for a team in the War.
     */
    private HashMap<UUID,Participant> getTeamMap(Collection<UUID> participantUIDs, TeamType teamType){
        HashMap<UUID,Participant> participantHashMap = new HashMap<>();

        participantUIDs.forEach(playerUID->{
            Map.Entry<UUID,Participant> participantEntry = participantConstructor(playerUID, teamType);
            participantHashMap.put(participantEntry.getKey(),participantEntry.getValue());
        });
        return participantHashMap;
    }

    /**
     * Constructs a <code>Map.Entry</code> for the <code>participantHashMap</code>.
     * @param participantUUID The <code>UUID</code> of a participating <code>Player</code>.
     * @param teamType The <code>TeamType</code> of the participating <code>Player</code>.
     * @return A <code>Map.Entry</code> of the participating <code>Player</code>'s UUID and <code>OfflinePlayer</code>
     * instance.
     */
    private Map.Entry<UUID,Participant> participantConstructor(UUID participantUUID, TeamType teamType){
        Participant participant;
        OfflinePlayer participatingPlayer = Bukkit.getOfflinePlayer(participantUUID);

        if(teamType.equals(TeamType.ATTACKING_TEAM)){
            participant = new Attacker(participatingPlayer,getWarName());
            return new AbstractMap.SimpleEntry<>(participantUUID,participant);
        }

        participant = new Defender(participatingPlayer,getWarName());
        return new  AbstractMap.SimpleEntry<>(participantUUID,participant);
    }

    /**
     * Constructs a <code>Audience</code> for every <code>Participant</code> in the war.
     * @param attackingPlayerUIDs A <code>Collection</code> of UUID's for the participants of the attacking team.
     * @param defendingPlayerUIDs A <code>Collection</code> of UUID's for the participants of the defending team.
     * @return The <code>Audience</code> for every <code>Participant</code> in the war.
     */
    private Audience constructAudienceForWar(Collection<UUID> attackingPlayerUIDs, Collection<UUID> defendingPlayerUIDs){

        ArrayList<Player> playerArray = new ArrayList<>();
        Player playerToAdd;

        for (UUID playerUID : attackingPlayerUIDs) {
            playerToAdd = Bukkit.getPlayer(playerUID);

            if(playerToAdd != null){
                playerArray.add(Bukkit.getPlayer(playerUID));

            }

        }

        for (UUID playerUID : defendingPlayerUIDs) {
            playerToAdd = Bukkit.getPlayer(playerUID);

            if(playerToAdd != null){
                playerArray.add(Bukkit.getPlayer(playerUID));

            }

        }

        return Audience.audience(playerArray.toArray(new Player[0]));
    }

    /**
     * Constructs the <code>teamScoreMap</code> by initialising it with all <code>TeamType</code> values and a score of
     * 0.
     */
    private void constructInitialScoreMap(){
        for(TeamType teamType : TeamType.values()){
            this.teamScoreMap.put(teamType,0);
        }
    }


}
