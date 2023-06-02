package io.curiositycore.landlord.util.war;

import io.curiositycore.landlord.util.maths.ChunkManipulation;
import io.curiositycore.landlord.util.messages.MessageSender;
import io.curiositycore.landlord.util.war.participants.Participant;
import io.curiositycore.landlord.util.war.participants.TeamType;
import io.curiositycore.landlord.util.war.participants.combatstats.types.AttackerStatType;
import io.curiositycore.landlord.util.war.participants.combatstats.types.CombatStatType;
import io.curiositycore.landlord.util.war.participants.combatstats.types.DefenderStatType;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.ChunkCoordinate;
import me.angeschossen.lands.api.memberholder.MemberHolder;
import me.angeschossen.lands.api.war.War;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
        MemberHolder attacker = warToAdd.getAttacker();
        MemberHolder defender = warToAdd.getDefender();
        Collection<UUID> attackerUIDs = attacker.getTrustedPlayers();
        Collection<UUID> defenderUIDs = defender.getTrustedPlayers();
        World currentWorld = landsAPI.getLandByName(attacker.getName()).getSpawn().getWorld();

        Collection<ChunkCoordinate> attackerChunkCoords= landsAPI.
                getLandByName(attacker.getName()).
                getChunks(currentWorld);

        Collection<ChunkCoordinate> defenderChunkCoords= landsAPI.
                getLandByName(defender.getName()).
                getChunks(currentWorld);

        CustomWar customWarToAdd = new CustomWar(warToAdd,attackerUIDs,defenderUIDs,attackerChunkCoords,defenderChunkCoords);

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
     * Sets the overall <code>PlayerCombatStats</code> field within the chosen <code>CustomWar</code> instance.
     * @param additionalScore The change in score to apply to the specified stat.
     * @param combatStatType The type of combat statistic to be changed.
     */
    public void warStatChange(double additionalScore, CombatStatType combatStatType){
        this.warHashMap.get(warHashMap).
                        getAttackerStats().
                        statHashMapChange(combatStatType,additionalScore);
    }
    /**
     * Implements a change to the overall specific attacker statistic field within the chosen <code>CustomWar</code>
     * instance.
     * @param additionalScore The change in score to apply to the specified stat.
     * @param attackerStatType The type of combat statistic to be changed.
     */
    public void attackerWarStatChange(double additionalScore, AttackerStatType attackerStatType){
        this.warHashMap.get(warHashMap).
                getAttackerStats().
                changeSpecificStat(attackerStatType,additionalScore);
    }
    /**
     * Implements a change to the overall specific defender statistic field within the chosen <code>CustomWar</code>
     * instance.
     * @param additionalScore The change in score to apply to the specified stat.
     * @param defenderStatType The type of combat statistic to be changed.
     */
    public void defenderWarStatChange(double additionalScore, DefenderStatType defenderStatType){
        this.warHashMap.get(warHashMap).
                getDefenderStats().
                changeSpecificStat(defenderStatType,additionalScore);
    }

    /**
     * Returns the <code>Participant</code> the <code>Player</code> is a Participating in.
     * @param targetPlayer The <code>Player</code> being checked.
     * @return The <code>CustomWar</code> the <code>Player</code> is a Participating in.
     */
    public Participant participantTeam(Player targetPlayer){
        UUID playerUID = targetPlayer.getUniqueId();
        for(CustomWar warToCheck: this.warHashMap.values()){
            if(warHasPlayer(warToCheck,playerUID)){
                return getParticipant(warToCheck,playerUID);
            }

        }
        return null;

    }
    public CustomWar getCaptureBlockWar(Block block){
        int[] chunkCoordinates = ChunkManipulation.chunkCheck(block.getX(), block.getZ());
        for(CustomWar customWar : this.warHashMap.values()){
            if(isInWarChunks(chunkCoordinates,customWar.getAttackerChunks())|| isInWarChunks(chunkCoordinates,customWar.getDefenderChunks())){
                return customWar;
            }
        }
        return null;
    }


    public void endCustomWar(String warName, TeamType winningTeam){
        MemberHolder winningTeamMemberHolder;
        War warToEnd = this.warHashMap.get(warName).getAssociatedLandsWar();
        if(winningTeam.equals(TeamType.ATTACKING_TEAM)){
            winningTeamMemberHolder = warToEnd.getAttacker();
        }
        else{
            winningTeamMemberHolder = warToEnd.getDefender();
        }

        warToEnd.end(winningTeamMemberHolder,false,warToEnd.getReward(winningTeamMemberHolder));
    }

    private boolean isInWarChunks(int[] blockLocationArray,Collection<ChunkCoordinate> warLandChunks){
         return warLandChunks.stream().anyMatch(chunkCoordinate ->
                Arrays.equals(new int[] {chunkCoordinate.getX(), chunkCoordinate.getZ()}, blockLocationArray)
        );
    }
    /**
     * Checks to see if the <code>UUID</code> being checked belongs to a <code>Participant</code> of the
     * <code>CustomWar</code> being checked.
     * @param warToCheck The <code>CustomWar</code> being checked for participation.
     * @param playerUID The <code>UUID</code> of the <code>Player</code> being checked.
     * @return A <code>boolean</code> representing if the <code>UUID</code> belongs to a <code>Participant</code> of the
     * <code>CustomWar</code>
     */
    private boolean warHasPlayer(CustomWar warToCheck, UUID playerUID){
        return warToCheck.getAttackingPlayers().containsKey(playerUID) || warToCheck.getDefendingPlayers().containsKey(playerUID);
    }

    /**
     * Gets the <code>Participant</code> for the requested <code>Participant</code>.
     * @param customWar The <code>CustomWar</code> of the <code>Participant</code>.
     * @param playerUID The <code>UUID</code> of the <code>Participant</code>.
     * @return The <code>CombatStats</code> for the requested <code>Participant</code>.
     */
    private Participant getParticipant(CustomWar customWar, UUID playerUID){
        if(customWar.getAttackingPlayers().keySet().contains(playerUID)){
            return customWar.getAttackingPlayers().get(playerUID);
        }
        return customWar.getDefendingPlayers().get(playerUID);
    }
}