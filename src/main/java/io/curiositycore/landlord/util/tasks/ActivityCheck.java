package io.curiositycore.landlord.util.tasks;

import io.curiositycore.landlord.Landlord;
import io.curiositycore.landlord.util.api.coreprotect.CoreprotectLookups;
import io.curiositycore.landlord.util.config.enums.ActivityScanSettings;
import io.curiositycore.landlord.util.config.ConfigManager;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Runnable that checks the Activity of every land claim and, if every player is under the config-defined limit,
 * the claim is deleted.
 */
public class ActivityCheck implements Runnable{
    /**
     * The <code>LandsIntegration</code> instance, essentially the Lands API, being utilised within this check.
     * This will have been initialized <code>onEnable</code>.
     */
    private LandsIntegration landsAPI;
    /**
     * The <code>CoreProtectAPI</code> instance being utilised within this check. <br>
     * This will have been initialized <code>onEnable</code>.
     */
    private CoreProtectAPI coreProtectAPI;
    /**
     * A <code>HashMap</code> defining the <code>String</code> names of members of the <code>Land</code> as the <code>Key</code>
     * and the activity they have had within the config-defined number of minutes within the config-defined number of
     * days to be checked, as a <code>Long</code>.
     */
    private HashMap<String,Long> landMemberActivityMap;
    /**
     * Instance of the <code>ConfigManager</code> to be utilised within this check.
     */
    private ConfigManager configManager;
    /**
     * Period in which the activity is compared over.
     */
    private int daysToScan;
    /**
     * The activity time required of at least one player within a <code>Land</code> claim.
     */
    private int timeRequirementInMinutes;

    /**
     * Constructor which defines the Landlord <code>Plugin</code> instance.
     * @param landsAPI The <code>LandsIntegration</code> instance, essentially the Lands API.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance being checked.
     * @param landlordPlugin An instance of the <code>Landlord</code> class.
     */
    public ActivityCheck(LandsIntegration landsAPI,CoreProtectAPI coreProtectAPI, Landlord landlordPlugin) {

        this.landsAPI = landsAPI;
        this.coreProtectAPI = coreProtectAPI;
        this.configManager = landlordPlugin.getDefaultConfigManager();
        this.landMemberActivityMap = new HashMap<>();
        this.daysToScan = configManager.getInt(ActivityScanSettings.ACTIVITY_SCAN_RANGE.getPathArray());

        this.timeRequirementInMinutes = configManager.getInt(ActivityScanSettings.ACTIVITY_REQUIREMENT.getPathArray());
    }

    @Override
    public void run() {
        int defaultTimeValue = this.configManager.getInt(ActivityScanSettings.ACTIVITY_DEFAULT_SESSION_TIME.getPathArray());
        CoreprotectLookups coreprotectLookups = new CoreprotectLookups(coreProtectAPI);

        Collection<Land> landCollection = landsAPI.getLands();

        //Guard check for if there are 0 lands on the server (edge case)
        if(landCollection == null){
            return;
        }

        landCollection.forEach(land->{

            ArrayList<UUID> playerArrayList = land.getTrustedPlayers().stream().collect(Collectors.toCollection(ArrayList::new));

            for(UUID playerUID: playerArrayList){

                String landMemberName = Bukkit.getServer().getOfflinePlayer(playerUID).getName();

                if(landMemberActivityMap.containsKey(landMemberName)){
                    continue;
                }

                landMemberActivityMap.put(landMemberName,coreprotectLookups.playTimeLookup(landMemberName, daysToScan, defaultTimeValue));
                
            }

            if(!landMemberActivityMap.values().stream().anyMatch(activityTime-> ((Math.toIntExact(activityTime)) >= timeRequirementInMinutes))){

                deleteLand(land);

            }

        });

    }

    /**
     * Deletes the <code>Land</code> and logs the deletion to the console.
     * @param landToDelete The <code>Land</code> to delete.
     */
    private void deleteLand(Land landToDelete){

        // Very rarely write comments... but this delete method... yeah this method stung... thanks LandsAPI...-_-
        landToDelete.delete((LandPlayer) null);
        Bukkit.getLogger().info("[Landlord] The "+ landToDelete.getName()+" land has been deleted due to inactivity.");
    }


}
