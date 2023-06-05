package io.curiositycore.landlord.util.war;

import io.curiositycore.landlord.util.messages.MessageSender;
import io.curiositycore.landlord.util.war.participants.combatstats.teams.AttackingCustomWarTeam;
import io.curiositycore.landlord.util.war.participants.combatstats.teams.CustomWarTeam;
import io.curiositycore.landlord.util.war.participants.combatstats.teams.DefendingCustomWarTeam;
import io.curiositycore.landlord.util.war.participants.combatstats.teams.enums.TeamType;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.ChunkCoordinate;
import me.angeschossen.lands.api.war.War;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Defines the team compositions and statistics for a Custom War occurring on the server.
 */
public class CustomWar {
    /**
     * The name of the <code>Land</code> attacking.
     */
    private final String primaryAttackerName;
    /**
     * The name of the <code>Land</code> being attacked.
     */
    private final String primaryDefenderName;
    private final War associatedLandsWar;
    private Audience participantAudience;
    private final MessageSender participantMessageSender;
    private HashMap<String, CustomWarTeam> teamMap;
    private final HashMap<TeamType, Integer> teamScoreMap = new HashMap<>();
    private boolean isEnded = false;

    /**
     * Constructor that initializes an <code>ArrayList</code> of <code>Player</code> instances, in
     * addition to <code>PlayerCombatStats</code> for both the attacking and defending teams.
     */
    public CustomWar(War associatedLandsWar, LandsIntegration landsAPI){
        this.associatedLandsWar = associatedLandsWar;
        this.primaryAttackerName = associatedLandsWar.getAttacker().getName();
        this.primaryDefenderName = associatedLandsWar.getDefender().getName();

        World currentWorld = Objects.requireNonNull(landsAPI.getLandByName(this.getPrimaryAttackerName()).getSpawn()).getWorld();

        this.teamMap = initPrimaryTeamEntries(landsAPI,currentWorld);
        this.participantAudience = constructAudienceForWar(landsAPI);
        this.participantMessageSender = new MessageSender(this.participantAudience);


        constructInitialScoreMap();
    }

    /**
     * Gets the <code>HashMap</code> for the various <code>CustomWarTeam</code> instances participating within the
     * <code>CustomWar</code>
     * @return The <code>HashMap</code> for the various <code>CustomWarTeam</code>s.
     */
    public HashMap<String,CustomWarTeam> getTeamMap(){return teamMap;}

    /**
     * Gets the attacking team's name.
     * @return The attacking team's name.
     */
    public String getPrimaryAttackerName(){return this.primaryAttackerName;}

    /**
     * Gets the defending team's name.
     * @return The defending team's name.
     */
    public String getPrimaryDefenderName(){return this.primaryDefenderName;}

    /**
     * Gets the name of the Custom War, as a <code>String</code>.
     * @return The name of the Custom War.
     */
    public String getWarName(){
        return primaryAttackerName +" vs "+ primaryDefenderName;
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
        return this.participantAudience;
    }

    public boolean getIsEnded(){
        return this.isEnded;
    }

    private HashMap<String,CustomWarTeam> initPrimaryTeamEntries(LandsIntegration landsAPI, World currentWorld){
        HashMap<String,CustomWarTeam> customWarTeamMap = new HashMap<>();
        Collection<ChunkCoordinate> attackerCoordinates = Objects.
                requireNonNull(landsAPI.getLandByName(primaryAttackerName)).
                getChunks(currentWorld);

        Collection<ChunkCoordinate> defenderCoordinates = Objects.
                requireNonNull(landsAPI.getLandByName(primaryDefenderName)).
                getChunks(currentWorld);

        customWarTeamMap.put(this.primaryAttackerName,
                new AttackingCustomWarTeam(this.associatedLandsWar, attackerCoordinates, this.getWarName()));

        customWarTeamMap.put(this.primaryDefenderName,
                new DefendingCustomWarTeam(this.associatedLandsWar, defenderCoordinates, this.getWarName()));

        return customWarTeamMap;
    }

    /**
     * Constructs a <code>Audience</code> for every <code>Participant</code> in the war.
     * @param landsAPI An instance of the API for the<code>Lands Plugin</code>
     * @return The <code>Audience</code> for every <code>Participant</code> in the war.
     */
    private Audience constructAudienceForWar(LandsIntegration landsAPI){
        Player playerToAdd;
        Collection<UUID> attackingPlayerUIDs = Objects.requireNonNull(landsAPI.
                getLandByName(primaryAttackerName)).
                getTrustedPlayers();

        Collection<UUID> defendingPlayerUIDs = Objects.requireNonNull(landsAPI.
                getLandByName(primaryDefenderName)).
                getTrustedPlayers();

        ArrayList<Player> playerArray = new ArrayList<>();


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
        return Audience.audience(playerArray);
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
    public void setParticipantAudience(Player playerToAdd){
        this.participantAudience = Audience.audience(this.participantAudience,playerToAdd);
    }

}
