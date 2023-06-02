package io.curiositycore.landlord.commands.subcommands;

import io.curiositycore.landlord.Landlord;
import io.curiositycore.landlord.util.api.coreprotect.CoreprotectLookups;
import io.curiositycore.landlord.util.config.settings.ActivityScanSettings;
import io.curiositycore.landlord.util.config.ConfigManager;
import io.curiositycore.landlord.util.messages.MessageSender;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;


import java.util.*;

/**
 * Checks for the individual activity of players from a command sender designated land.
 */
public class IndividualLandActivityChecker extends SubCommand{
    /**
     * The <code>LandsIntegration</code> instance, essentially the Lands API.
     */
    private LandsIntegration landsAPI;
    /**
     * Instance of the <code>CoreprotectLookups</code> class. Used for doing lookups via the <code>CoreProtectAPI</code>.
     */
    private CoreprotectLookups coreprotectLookups;
    /**
     * Instance of the <code>ConfigManager</code> being utilised within <code>Plugin</code> initialization..
     */
    private ConfigManager configManager;
    /**
     * Number of previous days to scan when conducting an activity lookup.
     */
    private int daysToScan;
    /**
     * Number of minutes to add to an activity scan if a session with a server crash is detected.
     */
    private int defaultTimeValue;

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
        this.daysToScan = this.configManager.getInt(ActivityScanSettings.ACTIVITY_SCAN_RANGE.getPathArray());
        this.defaultTimeValue = this.configManager.getInt(ActivityScanSettings.ACTIVITY_DEFAULT_SESSION_TIME
                                                                              .getPathArray());

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
    public void perform(CommandSender commandSender, String[] arguments) {

        if(consoleExecutedCommand(commandSender)){
            return;
        }

        MessageSender messageSender = new MessageSender(Bukkit.getPlayer(commandSender.getName()));
        int rankingNumber = 0;

        if(arguments.length < 2){
            messageSender.basicErrorMessage("Not enough arguments!","Try: "+syntax);
            return;
        }

        HashMap<String,Long> activityTimeHashmap = new HashMap<>();
        Land landToScan = landsAPI.getLandByName(arguments[1]);

        if(landToScan == null){
            messageSender.basicErrorMessage("A land called '"+arguments[1]+"' does not exist!",
                    "Please only input existing lands.");
            return;
        }

        landToScan.getTrustedPlayers().forEach(playerUID->{
            String playerName = Bukkit.getOfflinePlayer(playerUID).getName();

            activityTimeHashmap.put(playerName, coreprotectLookups.playTimeLookup(playerName,daysToScan,defaultTimeValue));
        });

        List<Map.Entry<String, Long>> landMemberActivityList = new ArrayList<>(activityTimeHashmap.entrySet());
        Collections.sort(landMemberActivityList, (o1, o2) -> o2.getValue().compareTo(Long.valueOf(o1.getValue())));

        messageSender.leaderboardHeaderCreation("Land Activity for: "+landToScan.getName());


        for(Map.Entry<String, Long> landMember : landMemberActivityList){
            rankingNumber += 1;
            messageSender.leaderboardMessage(String.valueOf(rankingNumber),landMember.getKey(),landMember.getValue()+" minutes.");
        }

    }


}
