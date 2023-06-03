package io.curiositycore.landlord.events.war;

import io.curiositycore.landlord.events.custom.CustomWarScoreEvent;
import io.curiositycore.landlord.util.config.ConfigManager;
import io.curiositycore.landlord.util.config.settings.CustomWarSettings;
import io.curiositycore.landlord.util.sounds.WarSounds;
import io.curiositycore.landlord.util.war.CustomWar;
import io.curiositycore.landlord.util.war.WarManager;
import io.curiositycore.landlord.util.war.participants.Participant;
import io.curiositycore.landlord.util.war.participants.combatstats.teams.enums.TeamType;
import io.curiositycore.landlord.util.war.participants.combatstats.types.CombatStatType;
import io.curiositycore.landlord.util.war.scoring.areas.sources.CaptureBlockSource;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.events.war.WarDeclareEvent;
import me.angeschossen.lands.api.events.war.WarEndEvent;
import me.angeschossen.lands.api.events.war.WarStartEvent;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;

/**
 * Listeners for any events to do with Custom Wars and those participating in those wars.
 */
public class WarEvents implements Listener {

    /**
     * Instance of the <code>ConfigManager</code> to be utilised within any <code>Listener</code> within this class.
     */
    ConfigManager configManager;

    /**
     * An instance of the <code>WarManager</code> class, to ensure that all <code>CustomWar</code> instances effected
     * by events are managed collectively.
     */
    WarManager warManager;

    /**
     * Constructor that initialises the various APIs and the <code>WarManager</code>
     * @param landsAPI The <code>LandsIntegration</code> instance, essentially the Lands API.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance for this <code>Plugin</code>.
     */
    public WarEvents(LandsIntegration landsAPI, CoreProtectAPI coreProtectAPI, ConfigManager configManager){
        this.warManager = new WarManager(coreProtectAPI,landsAPI);
        this.configManager = configManager;
    }

    /**
     * Listener for any time the owner of a <code>Land</code> claim declares war against another
     * <code>Land</code> claim. <br><i>(This Listener is for future use in a potential logging feature)</i>
     * @param warDeclareEvent An <code>Event</code> that occurs when a <code>Land</code> declares war.
     */
    @EventHandler
    public void warDeclarationEvent(WarDeclareEvent warDeclareEvent){


    }

    /**
     * Listener for any time a <code>War</code> is about to begin.<br><i>(This Listener adds a <code>CustomWar</code>
     * instance to the <code>HashMap</code> of the <code>warManager</code>)</i>
     * @param warStartEvent An <code>Event</code> that occurs when a <code>War</code> is starting.
     */
    @EventHandler
    public void warStartEvent(WarStartEvent warStartEvent){
        warManager.addWarToHashMap(warStartEvent.getWar());

    }

    /**
     * Listener for any time a <code>War</code> is about to end.<br><i>(This Listener is for future use in logging
     * functionality, and will be used to ensure any logs are correctly stored in the database correctly)</i>
     * @param warEndEvent An <code>Event</code> that occurs when a <code>War</code> is ending.
     */
    @EventHandler
    public void warEvent(WarEndEvent warEndEvent){

    }
    //TODO ensure that you investigate if the beacon can have different effects than vanilla.
    //TODO Add check to make sure only X caps can be placed at a time and only X distance away from each other
    //TODO Add check to make sure only outermost capture blocks are allowed to be placed OR just have it detect the
    //     Lands capture block.

    /**
     * Listener for placed blocks within a <code>CustomWar</code>. <i>(May be depreciated now the cap block place event exists)</i>
     * @param blockPlaceEvent An <code>Event</code> that occurs when a block is placed. Functionality only triggers if
     *                        block was placed within a custom war.
     */
    @EventHandler
    public void placeBlock(BlockPlaceEvent blockPlaceEvent){
        Block placedBlock = blockPlaceEvent.getBlockPlaced();

        if(!placedBlock.getType().equals(Material.BEACON)){
            Bukkit.getLogger().info(placedBlock.getType().name());
            return;
        }

        CustomWar warForCaptureBlock = warManager.getCaptureBlockWar(placedBlock);
        if(warForCaptureBlock == null){
            Bukkit.getLogger().info("TestToSeeIfFucked");
            return;
        }
        Bukkit.getLogger().info("Test to see if made it to the block source");
        CaptureBlockSource captureBlockSource = new CaptureBlockSource(this.configManager,
                                                                       placedBlock.getLocation(), warForCaptureBlock);
        Bukkit.getLogger().info("Test to see if made it to the timer.");
        captureBlockSource.startAreaTimer();


    }
    //TODO thank god
    //@EventHandler
    //public void placeCaptureFlag(CaptureFlagPlaceEvent captureFlagPlaceEvent){

    //}

    /**
     * Listener for any time an <code>EntityDamageByEntityEvent</code> triggers.<br><i>(This Listener ensures the
     * damage amount is added to the correct <code>CombatStats</code> of the <code>Participants</code> involved in
     * the event.)</i>
     * @param damageByEntityEvent An <code>Event</code> that occurs when an <code>Entity</code> is damaged by another
     *                            <code>Entity</code>
     */
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent damageByEntityEvent){
        double damageDealt = damageByEntityEvent.getDamage();
        Participant[] eventParticipants = getParticipantsOfEvent(damageByEntityEvent.getDamager(),
                                                                          damageByEntityEvent.getEntity());

        if (eventParticipants == null){
            return;
        }
        binaryCombatStatChange(eventParticipants,damageDealt,CombatStatType.DAMAGE_DEALT,CombatStatType.DAMAGE_TAKEN);

    }
    /**
     * Listener for any time a <code>Player</code> dies.<br><i>(This Listener ensures that, if killed during a war
     * that the correct kill amount and death amount values are added to the correct <code>CombatStats</code> of the
     * <code>Participants</code> involved in the event.) </i>
     * @param playerDeathEvent An <code>Event</code> that occurs when a <code>Player</code> is killed.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent){
        Player deathTarget = playerDeathEvent.getPlayer();
        Player deathSource = deathTarget.getKiller();

        Participant[] eventParticipants = getParticipantsOfEvent(deathTarget,deathSource);

        if (eventParticipants == null){
            return;
        }
        binaryCombatStatChange(eventParticipants, 1.0,CombatStatType.DEATHS,CombatStatType.KILLS);
    }

    /**
     * Listener for when a team in a <code>CustomWar</code> scores points. Ensures the point is added to.
     * @param customWarScoreEvent An <code>Event</code> called when a team has scored points in a <code>CustomWar</code>.
     */
    @EventHandler
    public void onCustomWarScoreEvent(CustomWarScoreEvent customWarScoreEvent){
        int scoredPoints = this.configManager.getInt(CustomWarSettings.POINTS_FROM_AREA_CAPTURE.getPathArray());
        TeamType teamThatScored = customWarScoreEvent.getTeamThatScored();
        CustomWar scoringWar = this.warManager.getWar(customWarScoreEvent.getAreaWarName());

        HashMap<TeamType,Integer> scoringWarTeamScoreMap = scoringWar.getTeamScoreMap();
        int newScore = scoringWarTeamScoreMap.get(teamThatScored)+ scoredPoints;
        scoringWarTeamScoreMap.put(teamThatScored,newScore);

        if(newScore >= configManager.getInt(CustomWarSettings.POINTS_REQUIRED_FOR_VICTORY.getPathArray())){
            this.warManager.endCustomWar(customWarScoreEvent.getAreaWarName(),customWarScoreEvent.getTeamThatScored());
            return;
        }

        scoringWar.getParticipantAudience().playSound(WarSounds.AREA_CAPTURED.getSound());

        try{
            scoringWar.getParticipantMessageSender().basicPluginPlayerMessage(getTeamTypeName(teamThatScored,scoringWar)+" has scored a point!");
            scoringWar.getParticipantMessageSender().basicPluginPlayerMessage("Current Score: "+scoringWarTeamScoreMap.get(teamThatScored));
        }
        catch(NullPointerException exception){
            Bukkit.getLogger().info("Null lol get wrecked");
        }

    }

    /**
     * Checks if the <code>Player</code> entities being checked are participating within the same <code>CustomWar</code>.
     * @param sourcePlayerWarName The name of the <code>CustomWar</code> of the player who caused the event.
     * @param targetPlayerWarName The name of the <code>CustomWar</code> of the player effected by the event.
     * @return A <code>boolean</code> representing if the <code>Player</code> entities are participating in the same war.
     */
    private boolean bothPlayersInTheSameWar(String sourcePlayerWarName, String targetPlayerWarName){
        if(sourcePlayerWarName == null || targetPlayerWarName == null){
            return false;
        }

        return sourcePlayerWarName.equalsIgnoreCase(targetPlayerWarName);
    }

    /**
     * Checks to see if the target and source of the event being checked involve <code>Player</code> instances.
     * @param targetEntity The entity being hit.
     * @param sourceEntity The event that hit the target.
     * @return The hypothetical <code>Player</code> instances involved in the event, as an array.
     */
    private Player[] isHitByPlayer(Entity targetEntity, Entity sourceEntity){

        if((targetEntity instanceof Player sourcePlayer
                && sourceEntity instanceof Player targetPlayer)){
            return new Player[]{targetPlayer,sourcePlayer};
        }

        if(!(targetEntity instanceof Player targetPlayer && sourceEntity instanceof Projectile sourceProjectile)){
            return null;
        }
        if(sourceProjectile.getShooter() instanceof Player sourcePlayer){
            return new Player[]{targetPlayer,sourcePlayer};
        }
        return null;
    }


    /**
     * Gets the <code>Participant</code> instances of both target and source of an event.<br> <i>(Will return null if
     * the target and source are not part of a currently active <code>CustomWar</code>)</i>
     * @param targetEntity The <code>Entity</code> that caused the event.
     * @param sourceEntity The <code>Entity</code> effected by the event.
     * @return A <code>Participant[]</code> containing both of the Entities' respective <code>Participant</code>
     * instances.
     */
    private Participant[] getParticipantsOfEvent(Entity targetEntity, Entity sourceEntity){
        Player[] playerArray = isHitByPlayer(targetEntity,sourceEntity);
        if(playerArray == null){
            return null;
        }
            Participant targetParticipant = this.warManager.getCorrespondingParticipant(playerArray[0]);
            Participant sourceParticipant = this.warManager.getCorrespondingParticipant(playerArray[1]);
        try{
        if(!bothPlayersInTheSameWar(sourceParticipant.getCustomWarName(),targetParticipant.getCustomWarName())){
            return null;
        }}
        catch(NullPointerException nullPointerException){
            return null;
        }
        return new Participant[]{sourceParticipant,targetParticipant};
    }

    /**
     * Applies changes to the <code>CombatStats</code> of the source and target <code>Participant</code> instances
     * involved with a called war event.
     * @param eventParticipants The <code>Participant[]</code> that represents the <code>Participant</code> instances
     * involved.
     * @param statChange The amount by which a Combat Statistic is to change.
     * @param sourceStatType The <code>CombatStatType</code> being changed in the source's <code>CombatStats</code>.
     * @param targetStatType The <code>CombatStatType</code> being changed in the target's <code>CombatStats</code>.
     */
    private void binaryCombatStatChange(Participant[] eventParticipants, Double statChange,
                                        CombatStatType sourceStatType, CombatStatType targetStatType){

        eventParticipants[0].getCombatStats().statHashMapChange(sourceStatType,statChange);
        eventParticipants[1].getCombatStats().statHashMapChange(targetStatType,statChange);
        //TODO when doing the end-user (like the GUI side of things / display of the values, ensure DecimalFormat_
        //     _is utilised so you dont get a large amount of decimal numbers as it looks silly.
        System.out.println("The "+ targetStatType.name()+ " for "+eventParticipants[1].getName()+" has changed to: "+
                eventParticipants[1].getCombatStats().getCombatStat(targetStatType));
        System.out.println("The "+ sourceStatType.name()+ " for "+eventParticipants[0].getName()+" has changed to: "+
                eventParticipants[0].getCombatStats().getCombatStat(sourceStatType));
    }

    /**
     * Gets the <code>Land</code> name of the <code>TeamType/code> for the requested <code>CustomWar</code>
     * @param teamType The <code>TeamType/code> for the requested <code>CustomWar</code>;
     * @param customWar The current <code>CustomWar</code> instance.
     * @return The <code>Land</code> name of the requested <code>TeamType/code>.
     */
    private String getTeamTypeName(TeamType teamType,CustomWar customWar){
        if(teamType.equals(TeamType.ATTACKING_TEAM)){
            return customWar.getPrimaryAttackerName();
        }
        else if(teamType.equals(TeamType.DEFENDING_TEAM)){
            return customWar.getPrimaryDefenderName();
        }
        return null;
    }


}
