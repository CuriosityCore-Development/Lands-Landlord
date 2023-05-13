package io.curiositycore.landlord.commands.subcommands;

import io.curiositycore.landlord.util.messages.PlayerMessages;

import me.angeschossen.lands.api.LandsIntegration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;


import java.util.*;

/**
 * Sends a scoreboard of <code>Land</code> upkeep values to the command sender via the <code>PlayerMessages</code> class.
 */
public class UpkeepChecker extends SubCommand{

    /**
     * The <code>LandsIntegration</code> instance, essentially the Lands API.
     */
    private final LandsIntegration landsAPI;

    /**
     * Constructor that initiates the Lands API.
     * @param landsAPI The <code>LandsIntegration</code> instance, essentially the Lands API.
     */
    public UpkeepChecker(LandsIntegration landsAPI){
        this.landsAPI = landsAPI;
    }

    @Override
    public String getName() {
        return "upkeeptop";
    }

    @Override
    public String getDescription() {
        return "Messages the sender a top 10 list of land upkeeps";
    }

    @Override
    public String getSyntax() {
        return "/landlord upkeeptop";
    }

    @Override
    public void perform(CommandSender commandSender, String[] arguments) {
        if(consoleExecutedCommand(commandSender)){
            return;
        }

        HashMap<String,Double> landUpkeepHashMap = new HashMap<>();
        PlayerMessages playerMessages = new PlayerMessages(Bukkit.getPlayer(commandSender.getName()));
        int sentMessageAmount = 0;

        landsAPI.getLands().forEach(land->landUpkeepHashMap.put(land.getName(),land.getUpkeepCosts()));

        List<Map.Entry<String, Double>> landUpkeepList = new ArrayList<>(landUpkeepHashMap.entrySet());
        Collections.sort(landUpkeepList, (o1, o2) -> o2.getValue().compareTo(Double.valueOf(o1.getValue())));
        playerMessages.leaderboardHeaderCreation("Land Upkeep Summary");

        for (Map.Entry<String, Double> entry : landUpkeepList) {

            if(sentMessageAmount >= 10){
                return;
            }
            sentMessageAmount += 1;


            playerMessages.leaderboardMessage(String.valueOf(sentMessageAmount),
                                                    entry.getKey(),
                                                    entry.getValue().toString());

        }

    }
}
