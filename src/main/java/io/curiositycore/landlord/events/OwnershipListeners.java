package io.curiositycore.landlord.events;

import io.curiositycore.landlord.Landlord;
import io.curiositycore.landlord.util.api.coreprotect.CoreprotectLookups;
import io.curiositycore.landlord.util.config.enums.ActivityScanSettings;
import io.curiositycore.landlord.util.config.ConfigManager;
import io.curiositycore.landlord.util.messages.PlayerMessages;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.events.LandCreateEvent;
import me.angeschossen.lands.api.events.LandOwnerChangeEvent;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.player.LandPlayer;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Listeners for any events to do with the config-defined <code>Land</code> owner limit.
 */
public class OwnershipListeners implements Listener {
    /**
     * An instance of the <code>Landlord</code> class.
     */
    private JavaPlugin landlordPlugin;
    /**
     * The <code>LandsIntegration</code> instance, essentially the Lands API, being utilised by the
     * <code>Listener</code>s. This will have been initialized <code>onEnable</code>.
     */
    private LandsIntegration landsAPI;

    /**
     * The <code>CoreProtectAPI</code> instance being utilised by the <code>Listeners</code>. This will have been
     * initialized <code>onEnable</code>.
     */
    private CoreProtectAPI coreProtectAPI;
    /**
     * The limit on the amount of <code>Land</code> claims a <code>Player</code> can own.
     */
    private int ownedLandsLimit;
    /**
     * An instance of the <code>PlayerMessages</code> class, used to define any messages sent to a <code>Player</code>
     * who triggers a <code>Listener</code> within this class.
     */
    private PlayerMessages playerMessages;
    /**
     * Instance of the <code>ConfigManager</code> to be utilised within any <code>Listener</code> within this class.
     */
    private ConfigManager configManager;


    /**
     * /**
     * Constructor for the <code>Listener</code>. Ensures the various APIs are set along with config defined
     * <code>Land</code>Owner limit.
     * @param landlordPlugin An instance of the <code>Landlord</code> class.
     * @param landsAPI The <code>LandsIntegration</code> instance, essentially the Lands API.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance being checked.
     */
    public OwnershipListeners(Landlord landlordPlugin, LandsIntegration landsAPI, CoreProtectAPI coreProtectAPI){
        this.configManager = landlordPlugin.getDefaultConfigManager();
        this.landlordPlugin = landlordPlugin;
        this.landsAPI = landsAPI;
        this.coreProtectAPI = coreProtectAPI;
        this.ownedLandsLimit = getOwnedLandsLimit();
    }

    /**
     * Listener for any time a new <code>Land</code> is created. Ensures no land is created that would take a
     * <code>Player</code>
     * over the config-defined <code>Land</code> owner limit.
     * @param landCreateEvent An <code>Event</code> that occurs when a <code>Player</code> tries to create a
     *                       new <code>Land </code>
     */
    @EventHandler
    public void onCreateLand(LandCreateEvent landCreateEvent){
        CoreprotectLookups coreprotectLookups = new CoreprotectLookups(coreProtectAPI);
        LandPlayer initiatingLandPlayer = landCreateEvent.getLandPlayer();
        Player initiatingPlayer = initiatingLandPlayer.getPlayer();
        int days = configManager.getInt(ActivityScanSettings.ACTIVITY_SCAN_RANGE.getPathArray());
        int timeRequirementInMinutes = configManager.getInt(ActivityScanSettings.ACTIVITY_REQUIREMENT.getPathArray());

        this.playerMessages = new PlayerMessages(initiatingPlayer);

        if(landOwnerShipLimitCheck(initiatingLandPlayer)){
            playerMessages.basicPluginPlayerMessage("Land creation cancelled, creation would take you over the current" +
                    " land ownership limit of: " + ownedLandsLimit);
            landCreateEvent.setCancelled(true);
        }

        if(coreprotectLookups.playTimeLookup(initiatingPlayer.getName(),days) < timeRequirementInMinutes){
            playerMessages.basicPluginPlayerMessage("Land creation cancelled, you have not met the minimum of " +
                    timeRequirementInMinutes + " minutes in the last " + days + " days!");
            landCreateEvent.setCancelled(true);

        };

    }

    /**
     * Getter for the config-defined <code>Land</code> ownership limit.
     * @return The <code>Land</code> ownership limit as an <code>int</code>
     */
    private int getOwnedLandsLimit(){
        return configManager.getInt("owner_limit","land_ownership_limit");
    }
    /**
     * Listener for any time a new <code>Land</code> undergoes an ownership transfer. Ensures no transfer is executed
     * that would take a <code>Player</code> over the config-defined <code>Land</code> owner limit.
     * @param landOwnerChangeEvent An <code>Event</code> that occurs when a <code>Player</code> tries to transfer
     * ownership of a <code>Land </code> to another <code>Player</code>.
     */
    @EventHandler
    public void onOwnershipTransfer(LandOwnerChangeEvent landOwnerChangeEvent){
        //TODO this method currently does not work, fix awaiting API developer support.
        Bukkit.getLogger().info("Test TEst Test");
        LandPlayer potentialNewOwner = landsAPI.getLandPlayer(landOwnerChangeEvent.getTargetUID());
        LandPlayer potentialOldOwner = landsAPI.getLandPlayer(landOwnerChangeEvent.getPlayerUID());
        Player oldOwnerPlayer = potentialOldOwner.getPlayer();





        if(landOwnerShipLimitCheck(potentialNewOwner)){

            landTransferalCancelMessage(landOwnerChangeEvent);

            landOwnerChangeEvent.setCancelled(true);
        }

    }

    /**
     * Checks if the <code>LandPlayer</code> is currently below the config-defined <code>Land</code> ownership limit.
     * @param playerToCheck The <code>LandPlayer</code> who is currently being checked.
     * @return A <code>boolean</code> that represents if the player is below the config defined <code>Land</code>
     * ownership limit.
     */
    private boolean landOwnerShipLimitCheck(LandPlayer playerToCheck){

        int ownedLands = 0;
        for(Land land : playerToCheck.getLands()){

            if(Bukkit.getOfflinePlayer(land.getOwnerUID()).getName() == playerToCheck.getPlayer().getName()){
                ownedLands += 1;
            }
        }

        return(ownedLands > ownedLandsLimit);

    }

    /**
     * Sends a message to either the player responsible for the <code>Land</code> transfer attempt, or to the console.
     * @param landOwnerChangeEvent An <code>Event</code> that occurs when a <code>Player</code> tries to transfer
     * ownership of a <code>Land </code> to another <code>Player</code>.
     */
    private void landTransferalCancelMessage(LandOwnerChangeEvent landOwnerChangeEvent){
        if(landOwnerChangeEvent.getReason() == LandOwnerChangeEvent.Reason.DEFAULT){
            playerMessages.basicPluginPlayerMessage("Transfer cancelled as transfer would cause new owner to be" +
                    "over their land ownership limit.");
            return;
        }
        else if(landOwnerChangeEvent.getReason() == LandOwnerChangeEvent.Reason.ADMIN){
            Bukkit.getLogger().info("[Landlord] Transfer cancelled for: " + landOwnerChangeEvent.getLand().getName()+
                                         " as transfer would cause new owner"+ landOwnerChangeEvent.getLandPlayer().getPlayer().getName()+
                                         " to be over their land ownership limit.");
            return;
        }

        Bukkit.getLogger().info("[Landlord] Transfer of land ownership for "+ landOwnerChangeEvent.getLand().getName()+
                                     " due to land ownership limit of "+ landOwnerChangeEvent.getLandPlayer().getPlayer().getName()+
                                     ". Reason for transferal: " + landOwnerChangeEvent.getReason().name());
    }
}

