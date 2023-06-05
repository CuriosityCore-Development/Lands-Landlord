package io.curiositycore.landlord.util.war.scoring.areas.tasks;

import io.curiositycore.landlord.events.custom.CustomWarScoreEvent;
import io.curiositycore.landlord.util.bars.AreaProgressBar;
import io.curiositycore.landlord.util.war.CustomWar;
import io.curiositycore.landlord.util.war.participants.Participant;
import io.curiositycore.landlord.util.war.participants.combatstats.teams.CustomWarTeam;
import io.curiositycore.landlord.util.war.scoring.areas.enums.AreaInfluenceType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

/**
 * Controls the repeated checks within an <code>ControllableArea</code> that has been activated during a
 * <code>CustomWar</code>.
 */
public class AreaControlChecker extends BukkitRunnable {
    /**
     * The <code>Location</code> of the source's central point.
     */
    protected Location sourceLocation;
    /**
     * The radius around the source in which a <code>Participant</code> counts towards capture.
     */
    protected int areaRadiusSquared;
    /**
     * The influence required to capture the area.
     */
    protected float requiredInfluence;
    protected String[] teamNameArray;
    protected CustomWar currentWar;
    /**
     * A <code>HashMap</code> with <code>OfflinePlayer</code> instance <code>Value</code>s and <code>Participant</code>
     * <code>Key</code>s for the attacking team.
     */
    private final HashMap<UUID,Participant> attackingPlayers;

    /**
     * A <code>HashMap</code> with <code>OfflinePlayer</code> instance <code>Value</code>s and <code>Participant</code>
     * <code>Key</code>s for the defending team.
     */
    private final HashMap<UUID,Participant> defendingPlayers;

    private final CaptureAreaEffect captureAreaEffect;

    /**
     * A <code>HashMap</code> with <code>Float</code> instance <code>Value</code>s and <code>AreaInfluenceType</code>
     * <code>Key</code>s to represent the amount of influence both teams have within the <code>ControllableArea</code>.
     */
    protected HashMap<AreaInfluenceType, Float> areaInfluenceTypeMap;
    /**
     * The <code>AreaProgressBar</code> instance being checked.
     */
    protected AreaProgressBar areaProgressBar;

    /**
     * Constructor that initialises both the war
     * @param currentWar the <code>CurrentWar</code> instance that the Capture Block is tied to.
     * @param requiredInfluence The influence required to capture the area.
     * @param areaRadiusSquared The radius around the source in which a <code>Participant</code> counts towards capture.
     * @param sourceLocation The <code>Location</code> of the source's central point.
     */
    public AreaControlChecker(CustomWar currentWar, float requiredInfluence, int areaRadiusSquared, Location sourceLocation){
        //TODO tidy up in future with less fields.
        HashMap<String, CustomWarTeam> currentWarTeamMap = currentWar.getTeamMap();
        this.teamNameArray = new String[]{currentWar.getPrimaryAttackerName(),currentWar.getPrimaryDefenderName()};
        this.sourceLocation = sourceLocation;
        this.areaRadiusSquared = areaRadiusSquared*areaRadiusSquared;
        this.areaInfluenceTypeMap = areaInfluenceTypeMapConstructor();
        this.requiredInfluence = requiredInfluence;
        this.attackingPlayers = currentWarTeamMap.get(currentWar.getPrimaryAttackerName()).getParticipantMap();
        this.defendingPlayers = currentWarTeamMap.get(currentWar.getPrimaryDefenderName()).getParticipantMap();
        this.areaProgressBar = new AreaProgressBar(currentWar);
        this.currentWar = currentWar;
        this.captureAreaEffect = new CaptureAreaEffect(sourceLocation,100,10);
    }
    @Override
    public void run() {
        int numberOfAttackers;
        int numberOfDefenders;
        AreaInfluenceType areaInfluenceTypeToAdd = null;
        numberOfAttackers = playerCheck(attackingPlayers);
        numberOfDefenders = playerCheck(defendingPlayers);
        this.areaProgressBar.changeProgressBarTitles(new int[]{numberOfAttackers,numberOfDefenders});
        if (numberOfAttackers > numberOfDefenders) {
            areaInfluenceTypeToAdd = AreaInfluenceType.ATTACKER_INFLUENCE;

        }
        else if (numberOfAttackers < numberOfDefenders) {
            areaInfluenceTypeToAdd = AreaInfluenceType.DEFENDER_INFLUENCE;

        }
        try{

            updateInfluenceValue(areaInfluenceTypeToAdd);
            this.captureAreaEffect.setAreaInfluenceType(areaInfluenceTypeToAdd);
            AreaInfluenceType capturingTeamType = getCapturingTeam();
            if (getCapturingTeam() != null) {

                this.areaProgressBar.deactivateBossBars();
                this.captureAreaEffect.cancel();
                this.cancel();
                Bukkit.getPluginManager().callEvent(new CustomWarScoreEvent(this.currentWar.getWarName(), getCapturingTeam().getTeamType()));
            }
        }
        catch(NullPointerException nullPointerException){
            this.captureAreaEffect.setAreaInfluenceType(null);
        }
    }

    /**
     * Gets the instance of the <code>AreaProgressBar</code> being checked.
     * @return The <code>AreaProgressBar</code> being checked.
     */
    public AreaProgressBar getAreaProgressBar(){
        return this.areaProgressBar;
    }
    public CaptureAreaEffect getCaptureAreaEffect(){return this.captureAreaEffect;}
    public Location getsourceLocation(){
        return this.sourceLocation;
    }
    /**
     * Check to see how many <code>Participant</code> instances of a singular team are within the radius of the
     * <code>ControllableArea</code> being checked.
     * @param teamParticipantMap A <code>HashMap</code> of a singular team within the <code>CustomWar</code> and their
     *                           <code>UUID</code> instances as <code>Key</code>s.
     * @return a <code>int</code> representing the number of <code>Participant</code> instances of a singular team are
     * in the area radius.
     */
    private int playerCheck(HashMap<UUID, Participant> teamParticipantMap) {
        int numberOfPlayers = 0;
        for (Participant participant : teamParticipantMap.values()) {
            OfflinePlayer participatingPlayer = participant.getPlayer();
            if (!participatingPlayer.isOnline()) {
                continue;
            }
            Bukkit.getLogger().info(participatingPlayer.getName()+": "+ participatingPlayer.getPlayer().getLocation().distanceSquared(this.sourceLocation)+" blocks away");
            if (participatingPlayer.getPlayer().getLocation().distanceSquared(this.sourceLocation) > this.areaRadiusSquared) {
                continue;
            }

            numberOfPlayers += 1;


        }
        return numberOfPlayers;
    }

    /**
     * Updates the progress of the <code>AreaProgressBar</code> being checked.
     * @param influenceType The <code>AreaInfluenceType</code> used to define which influence value has increased.
     */
    private void updateInfluenceValue(AreaInfluenceType influenceType) {
        float currentInfluence = areaInfluenceTypeMap.get(influenceType);

        currentInfluence += (1/this.requiredInfluence);

        areaInfluenceTypeMap.put(influenceType, currentInfluence);
        this.areaProgressBar.bossBarProgress(influenceType,currentInfluence);
    }

    /**
     * Checks to see if the required influence amount (as defined within the <code>Landlord Configuration File</code>
     * has been met by any of the teams within the <code>CustomWar</code>.
     * @return a <code>boolean</code> to represent if the required influence has been met by any of the teams within
     * the war.
     */
    private AreaInfluenceType getCapturingTeam(){
        for(AreaInfluenceType areaInfluenceType : areaInfluenceTypeMap.keySet()){

            if(this.areaInfluenceTypeMap.get(areaInfluenceType) > 1){
                return areaInfluenceType;
            }
        }
        return null;
    }

    protected HashMap<AreaInfluenceType,Float> areaInfluenceTypeMapConstructor(){
        HashMap<AreaInfluenceType, Float> areaInfluenceTypeIntegerHashMap = new HashMap<>();
        for(AreaInfluenceType areaInfluenceType : AreaInfluenceType.values()){
            areaInfluenceTypeIntegerHashMap.put(areaInfluenceType,0f);
        }
        return areaInfluenceTypeIntegerHashMap;
    }
}
