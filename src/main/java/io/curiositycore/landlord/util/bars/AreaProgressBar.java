package io.curiositycore.landlord.util.bars;

import io.curiositycore.landlord.util.war.CustomWar;
import io.curiositycore.landlord.util.war.scoring.areas.enums.AreaInfluenceType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * The progress bar linked to an <code>ControllableAreaSource</code>, used to display attacker and defender
 * <code>BossBar</code> instances to participants of the war.
 */
public class AreaProgressBar {
    /**
     * A <code>HashMap</code> containing the <code>BossBar</code> instances for the <code>ControllableAreaSource</code> as the
     * <code>Value</code> instances and the name of the team the bar is for as the <code>Key</code> instances.
     */
    HashMap<String,BossBar> bossBarHashMap;
    /**
     * An instance of the <code>Audience</code> to which the <code>BossBar</code> instances relate to. <i>(It is
     * assumed that the boss bar will want to be shown universally to all war participants)</i>
     */
    Audience bossBarAudience;
    /**
     * The name of the attacking participants.
     */
    String attackerName;
    /**
     * The name of the defending participants.
     */
    String defenderName;

    /**
     * Constructor which initialises the audience and team names.
     * @param currentWar The <code>CustomWar</code> instance for the war this area is related to.
     */
    public AreaProgressBar(CustomWar currentWar){
        //TODO could be worth making a method.
        Collection<UUID> attackingPlayerUIDs = currentWar.getAttackingPlayers().keySet();
        Collection<UUID> defendingPlayerUIDs = currentWar.getDefendingPlayers().keySet();
        Player[] participatingPlayersArray = playerArrayConstructor(attackingPlayerUIDs,defendingPlayerUIDs);
        this.attackerName = currentWar.getAttackerName();
        this.defenderName = currentWar.getDefenderName();
        this.bossBarAudience = Audience.audience(participatingPlayersArray);
        this.bossBarHashMap =bossBarConstructor();
    }

    /**
     * Shows the <code>BossBar</code> instances within the <code>bossBarHashMap</code> to the
     * <code>bossBarAudience</code>.
     */
    public void activateBossBars(){
        this.bossBarAudience.showBossBar(this.bossBarHashMap.get(attackerName));
        this.bossBarAudience.showBossBar(this.bossBarHashMap.get(defenderName));

    }
    /**
     * Hides the <code>BossBar</code> instances within the <code>bossBarHashMap</code> to the
     * <code>bossBarAudience</code>.
     */
    public void deactivateBossBars(){
        //TODO remove the debugging tries when checks for no players online are done
        try{
        this.bossBarAudience.hideBossBar(this.bossBarHashMap.get(attackerName));
        this.bossBarAudience.hideBossBar(this.bossBarHashMap.get(defenderName));}
        catch (Exception ignored){}

    }

    /**
     * Progresses the <code>BossBar</code> instances by the defined <code>currentInfluence</code> parameter.
     * @param areaInfluenceType The <code>AreaInfluenceType</code> to progress.
     * @param currentInfluence The influence value to update the <code>BossBar</code> to.
     */
    public void bossBarProgress(AreaInfluenceType areaInfluenceType, float currentInfluence){

        if(currentInfluence> 1.0f){
            currentInfluence = 1.0f;
        }
        if(areaInfluenceType.equals(AreaInfluenceType.ATTACKER_INFLUENCE)){
            this.bossBarHashMap.get(attackerName).progress(currentInfluence);
            return;
        }
        this.bossBarHashMap.get(defenderName).progress(currentInfluence);

    }
    public void changeProgressBarTitles(int[] teamScores){
        Component.text(this.defenderName+" players in area: "+teamScores[1]);
        this.bossBarHashMap.get(attackerName).name(Component.text(this.attackerName+" players in area: "+teamScores[0]));
        this.bossBarHashMap.get(defenderName).name(Component.text(this.defenderName+" players in area: "+teamScores[1]));
    }

    /**
     * Constructs the <code>HashMap</code> for the <code>BossBar</code> instances.
     * @return The completed <code>HashMap</code> for the <code>BossBar</code> instances.
     */
    private HashMap<String,BossBar> bossBarConstructor(){


        HashMap<String,BossBar> teamBossBarsHashMap = new HashMap<>();
        //Component component = Component.text("Capture Point Active at"+ blockLocation.toString());
        // TODO fix the attacker name and defender name.
        BossBar attackerBossBar = BossBar.bossBar(Component.text(attackerName),0,BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        BossBar defenderBossBar = BossBar.bossBar(Component.text(defenderName),0,BossBar.Color.BLUE,BossBar.Overlay.PROGRESS);
        teamBossBarsHashMap.put(this.attackerName,attackerBossBar);
        teamBossBarsHashMap.put(this.defenderName,defenderBossBar);

        return teamBossBarsHashMap;
    }

    /**
     * Constructs a <code>Player[]</code> for the entirety of the <code>Player</code> instances taking part within the
     * <code>CustomWar</code>.
     * @param attackingPlayerUIDs A <code>Collection</code> of UUID's for the participants of the attacking team.
     * @param defendingPlayerUIDs A <code>Collection</code> of UUID's for the participants of the defending team.
     * @return
     */
    private Player[] playerArrayConstructor(Collection<UUID> attackingPlayerUIDs, Collection<UUID> defendingPlayerUIDs){

        Player[] playerArray = new Player[attackingPlayerUIDs.size()+defendingPlayerUIDs.size()];
        int index = 0;
        for (UUID playerUID : attackingPlayerUIDs) {
            playerArray[index] = Bukkit.getPlayer(playerUID);
            index++;
        }

        for (UUID playerUID : defendingPlayerUIDs) {
            playerArray[index] = Bukkit.getPlayer(playerUID);
            index++;
        }

        return playerArray;
    }
}
