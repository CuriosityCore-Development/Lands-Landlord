package io.curiositycore.landlord.util.api.lands;

import io.curiositycore.landlord.Landlord;
import io.curiositycore.landlord.util.api.coreprotect.CoreprotectLookups;
import io.curiositycore.landlord.util.config.enums.ActivityScanSettings;
import io.curiositycore.landlord.util.config.ConfigManager;
import me.angeschossen.lands.api.land.Land;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Util that can check the acitvity of pre-defined players. <br> <i>(Activity Data is based on the CoreProtectAPI.sessionLookup method)</i>
 */
public class LandActivityChecker {
    /**
     * Instance of the <code>ConfigManager</code> to be utilised within the checker.
     */
    ConfigManager configManager;
    /**
     * A <code>HashMap</code> defining the <code>UUID</code> of members of the <code>Land</code> as the <code>Key</code>
     * and the activity they have had within the config-defined number of minutes within the config-defined number of
     * days to be checked, as a <code>Long</code>.
     */
    HashMap<UUID,Long> landMemberActivityMap;
    /**
     * A <code>HashMap</code> defining the <code>UUID</code> of members of the <code>Land</code> as the <code>Key</code>
     * and the activity they have had within the config-defined number of minutes within the config-defined number of
     * days to be checked, as a <code>Long</code>. <br>
     * <i>(The difference between this and the <code>landMemberActivityMap</code> is that this <code>HashMap</code>
     * only contains <code>UUID</code> keys that were taken from a filtered down <code>List</code> of the
     * members of <code>Land</code>)</i>
     */
    HashMap<UUID,Long> filteredMemberActivityMap;
    /**
     * The <code>CoreProtectAPI</code> instance being utilised within this check. This will have been
     * initialized <code>onEnable</code>.
     */
    CoreProtectAPI coreProtectAPI;

    /**
     * Constructor for the activity checker when there is no filtered list, hence just scanning every player in a
     * <code>Land</code>.
     * @param land The <code>Land</code> instance being checked.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance being checked.
     * @param landlordPlugin An instance of the <code>Landlord</code> class.
     */
    public LandActivityChecker(Landlord landlordPlugin,Land land, CoreProtectAPI coreProtectAPI){
        this.configManager = landlordPlugin.getDefaultConfigManager();
        this.coreProtectAPI = coreProtectAPI;
        this.landMemberActivityMap = constructLandMemberActivityMap(land);
        this.filteredMemberActivityMap = null;
    }
    /**
     * Constructor for the activity checker when there is a filtered list, hence scanning just the players in the
     * <code>ArrayList</code> filter.
     * @param land The <code>Land</code> instance being checked.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance being checked.
     * @param filterList An <code>ArrayList</code> of filtered <code>Player UUID</code>s.
     * @param landlordPlugin An instance of the <code>Landlord</code> class.
     */
    public LandActivityChecker(Landlord landlordPlugin,Land land, CoreProtectAPI coreProtectAPI, ArrayList<UUID> filterList){
        this.configManager = landlordPlugin.getDefaultConfigManager();
        this.coreProtectAPI = coreProtectAPI;
        this.landMemberActivityMap = constructLandMemberActivityMap(land);
        this.filteredMemberActivityMap = constructFilteredMemberActivityMap(land,filterList);
    }

    /**
     * Getter for the most active member within a land claim.
     * @param filtered the <code>boolean</code> that describes whether there is a filter for this check or not.
     * @param ownerUID the original owner of the <code>Land</code> being checked for activity.
     * @return The <code>UUID</code> of the most active member in the claim.
     */
    public UUID getMostActiveMember(boolean filtered, UUID ownerUID) {
        UUID mostActiveMemberUID = null;
        HashMap<UUID,Long> activityHashMap;
        activityHashMap = this.landMemberActivityMap;
        if(filtered && !this.filteredMemberActivityMap.isEmpty()){
            activityHashMap = this.filteredMemberActivityMap;
        }
        for(UUID landMemberUID : activityHashMap.keySet()){
            if(mostActiveMemberUID == null){
                mostActiveMemberUID = landMemberUID;
            }
            if(mostActiveMemberUID.equals(ownerUID)){
                continue;
            }

            if(activityHashMap.get(landMemberUID) > activityHashMap.get(mostActiveMemberUID)){
                mostActiveMemberUID = landMemberUID;
            }

        }
        return mostActiveMemberUID;
    }

    /**
     * Constructor for the filteredMemberActivityMap, a <code>HashMap</code> of <code>UUID</code> keys and
     * <code>Long</code> activity values, via an <code>ArrayList</code> of filtered <code>UUID</code>s.
     * @param land The <code>Land</code> instance currently being checked.
     * @param filteredArrayList The <code>ArrayList</code> of players to specifically check within the <code>Land</code>.
     * @return The <code>HashMap</code> filteredMemberActivityMap.
     */
    private HashMap<UUID,Long> constructFilteredMemberActivityMap(Land land, ArrayList<UUID> filteredArrayList){
        CoreprotectLookups coreprotectLookups = new CoreprotectLookups(this.coreProtectAPI);
        return getMemberActivityHashMap(coreprotectLookups, filteredArrayList);
    }
    /**
     * Constructor for the landMemberActivityMap, a <code>HashMap</code> of <code>UUID</code> keys and
     * <code>Long</code> activity values.
     * @param land The <code>Land</code> instance currently being checked.
     * @return The <code>HashMap</code> landMemberActivityMap.
     */
    private HashMap<UUID,Long> constructLandMemberActivityMap(Land land){
        CoreprotectLookups coreprotectLookups = new CoreprotectLookups(this.coreProtectAPI);
        ArrayList<UUID> landMemberArrayList = new ArrayList<>(land.getTrustedPlayers());
        return getMemberActivityHashMap(coreprotectLookups, landMemberArrayList);
    }

    /**
     * Getter for a <code>HashMap</code> that defines the <code>UUID</code> of each player within a
     * defined <code>ArrayList</code> and the ammount of minutes they have played for in the form of a <code>Long</code>.
     * @param coreprotectLookups The <code>CoreprotectLookups</code> instance to analyse.
     * @param landMemberArrayList The <code>ArrayList</code> of the <code>UUID</code>s to analyse.
     * @return The <code>HashMap</code> for the activity time for the <code>Land</code> members.
     */
    @NotNull
    private HashMap<UUID, Long> getMemberActivityHashMap(CoreprotectLookups coreprotectLookups, ArrayList<UUID> landMemberArrayList) {
        HashMap<UUID,Long> memberActivityMap = new HashMap<>();

        int days = configManager.getInt(ActivityScanSettings.ACTIVITY_SCAN_RANGE.getPathArray());
        int defaultTimeValue = this.configManager.getInt(ActivityScanSettings.ACTIVITY_DEFAULT_SESSION_TIME.getPathArray());
        for(UUID landMemberUID : landMemberArrayList){
            String landMemberName = Bukkit.getOfflinePlayer(landMemberUID).getName();
            memberActivityMap.put(landMemberUID,coreprotectLookups.playTimeLookup(landMemberName,days, defaultTimeValue));
        }
        return memberActivityMap;
    }
}
