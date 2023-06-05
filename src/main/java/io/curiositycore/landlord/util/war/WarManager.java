package io.curiositycore.landlord.util.war;

import io.curiositycore.landlord.util.maths.ChunkManipulation;
import io.curiositycore.landlord.util.war.participants.Participant;
import io.curiositycore.landlord.util.war.participants.combatstats.teams.CustomWarTeam;
import io.curiositycore.landlord.util.war.participants.combatstats.teams.enums.TeamType;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.ChunkCoordinate;
import me.angeschossen.lands.api.memberholder.MemberHolder;
import me.angeschossen.lands.api.war.War;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 Manager for any <code>CustomWar</code> instances currently active on the server. Used to control scores, rules and
 general mechanics for each War.
 */
public class WarManager {
    protected final CoreProtectAPI coreProtectAPI;
    /**
     * The <code>LandsIntegration</code> instance, essentially the Lands API, being utilised within the
     * <code>CommandManager</code>. This will have been initialized <code>onEnable</code>.
     */
    protected final LandsIntegration landsAPI;
    /**
     * A <code>HashMap</code> defining a current <code>CustomWar</code> instance as the
     * <code>Value</code> and the name of the <code>CustomWar</code> as the <code>Key</code>.
     */
    protected HashMap<String, CustomWar> warHashMap;



    /**
     * Constructor for the Manager, initializes the primary <code>API</code> instances and the <code>HashMap</code> for
     * every <code>CustomWar</code> instance active within the server.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance.
     * @param landsAPI The <code>LandsIntegration</code> instance, essentially the Lands API instance.
     */
    public WarManager(CoreProtectAPI coreProtectAPI,LandsIntegration landsAPI){
        this.landsAPI = landsAPI;
        this.coreProtectAPI = coreProtectAPI;
        this.warHashMap = new HashMap<>();
    }

    /**
     * Adds a <code>CustomWar</code> to the <code>warHashMap</code> field. <br><i>(Called primarily within the
     * <code>WarEvents</code> class)</i>
     * @param warToAdd the <code>War</code> to use for constructing the <code>CustomWar</code> instance.
     */
    public void addWarToHashMap(War warToAdd){
        CustomWar customWarToAdd = new CustomWar(warToAdd,this.landsAPI);
       this.warHashMap.put(customWarToAdd.getWarName(),customWarToAdd);
    }

    /**
     * Gets the requested <code>CustomWar</code> instance from the <code>warHashMap</code>, based on the War's name.
     * @param warName The <code>String</code> defining the name of the <code>CustomWar</code> instance.
     * @return The requested <code>CustomWar</code> instance.
     */
    public CustomWar getWar(String warName){
        return this.warHashMap.get(warName);
    }

    /**
     * Returns the <code>Participant</code> the <code>Player</code> is a Participating in.
     * @param targetPlayer The <code>Player</code> being checked.
     * @return The <code>CustomWar</code> the <code>Player</code> is a Participating in.
     */
    public @NotNull Participant getCorrespondingParticipant(Player targetPlayer){
        UUID playerUID = targetPlayer.getUniqueId();
        for(CustomWar warToCheck: this.warHashMap.values()){
            try{
            return getParticipant(warToCheck,playerUID);

            }
            catch (Exception exception){
                if(!exception.getClass().equals(NullPointerException.class)){
                    exception.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * Gets the <code>CustomWar</code> the capture block, if applicable, was placed.
     * @return The <code>CustomWar</code> in which the capture block was placed.
     */
    public CustomWar getCaptureBlockWar(Location location){
        int[] chunkCoordinates = ChunkManipulation.chunkCheck(location.getBlockX(), location.getBlockZ());

        for(CustomWar customWar : this.warHashMap.values()){
            String[] teamNameArray = new String[]{customWar.getPrimaryAttackerName(),customWar.getPrimaryDefenderName()};
            HashMap<String, CustomWarTeam> customWarTeamMap = customWar.getTeamMap();

            if(isInWarChunks(chunkCoordinates,customWarTeamMap.get(teamNameArray[0]).getChunkCoordinates())
                    ||
                    isInWarChunks(chunkCoordinates,customWarTeamMap.get(teamNameArray[1]).getChunkCoordinates())) {
                return customWar;
            }
        }
        return null;
    }

    /**
     * Ends the specified <code>CustomWar</code> instance, unregistering any tasks and ending the underlying
     * <code>War</code> instance.
     * @param warName The name of the <code>CustomWar</code>.
     * @param winningTeam The <code>TeamType</code> that won.
     */
    public void endCustomWar(String warName, TeamType winningTeam){
        MemberHolder winningTeamMemberHolder;
        War warToEnd = this.warHashMap.get(warName).getAssociatedLandsWar();
        winningTeamMemberHolder = winningTeam.getTeamMemberHolder(warToEnd);

        warToEnd.end(winningTeamMemberHolder,false,warToEnd.getReward(winningTeamMemberHolder));
        this.warHashMap.remove(warName);
    }
    public CustomWar getWarOfJoinedPlayer(UUID joinedPlayerUID){
        return warHashMap.values().stream().filter(warToCheck -> warToCheck.getTeamMap().values().stream().anyMatch(
                team -> team.getParticipantMap().containsKey(joinedPlayerUID))).findFirst().orElse(null);
    }

    /**
     * Checks if the location being checked is in the specified <code>WarCoordinate Collection</code>
     * @param blockLocationArray
     * @param warLandChunks
     * @return
     */
    private boolean isInWarChunks(int[] blockLocationArray,Collection<ChunkCoordinate> warLandChunks){
         return warLandChunks.stream().anyMatch(chunkCoordinate ->
                Arrays.equals(new int[] {chunkCoordinate.getX(), chunkCoordinate.getZ()}, blockLocationArray)
        );
    }

    /**
     * Gets the <code>Participant</code> for the requested <code>Participant</code>.
     * @param customWar The <code>CustomWar</code> of the <code>Participant</code>.
     * @param playerUID The <code>UUID</code> of the <code>Participant</code>.
     * @return The <code>CombatStats</code> for the requested <code>Participant</code>.
     */
    private Participant getParticipant(CustomWar customWar, UUID playerUID){
        String[] teamNameArray = new String[]{customWar.getPrimaryAttackerName(),customWar.getPrimaryDefenderName()};
        HashMap<String,CustomWarTeam> customWarTeamHashmap = customWar.getTeamMap();

        if(customWarTeamHashmap.get(teamNameArray[0]).getParticipantMap().get(playerUID) != null){
            return customWarTeamHashmap.get(teamNameArray[0]).getParticipantMap().get(playerUID);
        }
        return customWarTeamHashmap.get(teamNameArray[1]).getParticipantMap().get(playerUID);
    }


}