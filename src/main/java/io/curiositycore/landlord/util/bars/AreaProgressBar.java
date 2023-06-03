package io.curiositycore.landlord.util.bars;

import io.curiositycore.landlord.util.war.CustomWar;
import io.curiositycore.landlord.util.war.scoring.areas.enums.AreaInfluenceType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

/**
 * The progress bar linked to an <code>ControllableAreaSource</code>, used to display attacker and defender
 * <code>BossBar</code> instances to participants of the war.
 */
public class AreaProgressBar {
    /**
     * A <code>HashMap</code> containing the <code>BossBar</code> instances for the <code>ControllableAreaSource</code> as the
     * <code>Value</code> instances and the name of the team the bar is for as the <code>Key</code> instances.
     */
    private final HashMap<String,BossBar> bossBarHashMap;
    /**
     * An instance of the <code>Audience</code> to which the <code>BossBar</code> instances relate to. <i>(It is
     * assumed that the boss bar will want to be shown universally to all war participants)</i>
     */
    private final Audience bossBarAudience;
    /**
     * The name of the attacking participants.
     */
    private final String attackerName;
    /**
     * The name of the defending participants.
     */
    private final String defenderName;

    private BukkitTask coordinatesBarTask;


    /**
     * Constructor which initialises the audience and team names.
     * @param currentWar The <code>CustomWar</code> instance for the war this area is related to.
     */
    public AreaProgressBar(CustomWar currentWar){
        this.attackerName = currentWar.getPrimaryAttackerName();
        this.defenderName = currentWar.getPrimaryDefenderName();
        this.bossBarAudience = currentWar.getParticipantAudience();
        this.bossBarHashMap =bossBarConstructor();

    }

    /**
     * Shows the <code>BossBar</code> instances within the <code>bossBarHashMap</code> to the
     * <code>bossBarAudience</code>.
     */
    public void activateBossBars(Location areaSourceLocation){
        this.bossBarAudience.showBossBar(this.bossBarHashMap.get(attackerName));
        this.bossBarAudience.showBossBar(this.bossBarHashMap.get(defenderName));
        this.bossBarAudience.sendActionBar(getActionBarComponent(areaSourceLocation));
        this.coordinatesBarTask = Bukkit.getScheduler().runTaskTimer(Bukkit.getPluginManager().getPlugin("Landlord"), () -> {
            Component actionBarComponent = getActionBarComponent(areaSourceLocation);
            this.bossBarAudience.sendActionBar(actionBarComponent);
        }, 0L, 20L);
    }
    /**
     * Hides the <code>BossBar</code> instances within the <code>bossBarHashMap</code> to the
     * <code>bossBarAudience</code>.
     */
    public void deactivateBossBars(){
        //TODO remove the debugging tries when checks for no players online are done
        try{
        this.bossBarAudience.hideBossBar(this.bossBarHashMap.get(attackerName));
        this.bossBarAudience.hideBossBar(this.bossBarHashMap.get(defenderName));
        this.coordinatesBarTask.cancel();
        this.bossBarAudience.sendActionBar(Component.text(""));

        }
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
    private Component getActionBarComponent(Location location){
        return Component.text("Capture Point Active: ["+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ()+"]");
    }


}
