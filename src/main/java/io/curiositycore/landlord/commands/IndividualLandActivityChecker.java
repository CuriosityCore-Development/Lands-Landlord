package io.curiositycore.landlord.commands;

import io.curiositycore.landlord.Landlord;
import io.curiositycore.landlord.util.api.coreprotect.CoreprotectLookups;
import io.curiositycore.landlord.util.config.ConfigManager;
import io.curiositycore.landlord.util.messages.PlayerMessages;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;


import java.util.HashMap;

/**
 * Checks for the individual activity of players from a command sender designated land.
 */
public class IndividualLandActivityChecker extends SubCommand{
    /**
     * The <code>LandsIntegration</code> instance, essentially the Lands API.
     */
    LandsIntegration landsAPI;
    /**
     * Instance of the <code>CoreprotectLookups</code> class. Used for doing lookups via the <code>CoreProtectAPI</code>.
     */
    CoreprotectLookups coreprotectLookups;
    /**
     * Instance of the <code>ConfigManager</code> being utilised within <code>Plugin</code> initialization..
     */
    ConfigManager configManager;
    /**
     * Number of previous days to scan when conducting an activity lookup.
     */
    int daysToScan;
    HashMap<String , Long> activityTimeHashmap;

    /**
     * A <code>SubCommand</code> for sending a message detailing  activity (in minutes) for each player within a
     * specific <code>Land</code> claim to the command sender.
     * @param landlord The <code>Plugin</code> instance for the landlord Plugin.
     * @param landsAPI The <code>LandsIntegration</code> instance, essentially the Lands API.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance being checked.
     */
    public IndividualLandActivityChecker(Landlord landlord, LandsIntegration landsAPI, CoreProtectAPI coreProtectAPI){
        this.landsAPI = landsAPI;
        this.coreprotectLookups = new CoreprotectLookups(coreProtectAPI);
        this.configManager = new ConfigManager(landlord);
        this.daysToScan = this.configManager.getInt("activity_scan","activity_range");
    }

    @Override
    public String getName() {
        return "activitycheck";
    }

    @Override
    public String getDescription() {
        return "Checks the specified land for activity, as per requirements within the Configuration file.";
    }

    @Override
    public String getSyntax() {
        return "/landlord activitycheck <NameOfLand>";
    }

    @Override
    public void perform(CommandSender player, String[] arguments) {
        PlayerMessages playerMessages = new PlayerMessages(Bukkit.getPlayer(player.getName()));

        if(arguments[1]== null){
            playerMessages.basicErrorMessage("Not enough arguments!",syntax);
            return;
        }

        activityTimeHashmap = new HashMap<>();
        Land landToScan = landsAPI.getLandByName(arguments[1]);

        if(landToScan == null){
            playerMessages.basicErrorMessage("A land called '"+arguments[1]+"' does not exist!",
                    "Please only input existing lands.");
            return;
        }

        landToScan.getTrustedPlayers().forEach(playerUID->{
            String playerName = Bukkit.getOfflinePlayer(playerUID).getName();
            activityTimeHashmap.put(playerName, (long) Math.ceil(coreprotectLookups.playTimeLookup(playerName,daysToScan)/1000.0/60.0));
        });
        playerMessages.leaderboardHeaderCreation("Land Activity for: "+landToScan.getName());
        //TODO before doing this loop add function to order it via activity time.
        //TODO also add in the functionality for the increasing ranking.
        for(String playerName : activityTimeHashmap.keySet()){
            playerMessages.scoreboardMessage("",playerName,activityTimeHashmap.get(playerName)+" minutes.");
        }

    }


}
