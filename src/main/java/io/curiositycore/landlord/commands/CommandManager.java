package io.curiositycore.landlord.commands;

import io.curiositycore.landlord.Landlord;
import io.curiositycore.landlord.commands.subcommands.IndividualLandActivityChecker;
import io.curiositycore.landlord.commands.subcommands.LandOwnerLimitCheck;
import io.curiositycore.landlord.commands.subcommands.SubCommand;
import io.curiositycore.landlord.commands.subcommands.UpkeepChecker;
import io.curiositycore.landlord.util.messages.PlayerMessages;
import me.angeschossen.lands.api.LandsIntegration;

import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Manager for any commands within the Landlord <code>Plugin</code>. Commands within Landlord begins with <i>"landlord"</i>.
 */
public class CommandManager implements TabExecutor {
    /**
     * A <code>HashMap</code> defining the <code>SubCommand</code>s of <code>CommandManager</code> as the
     * <code>Value</code> and the in-game name of the <code>SubCommand</code> as the <code>Key</code>.
     */
    private HashMap<String, SubCommand> subCommandHashMap;
    /**
     * The <code>CoreProtectAPI</code> instance being utilised within the <code>CommandManager</code>. This will have
     * been initialized <code>onEnable()</code>.
     */
    private final  CoreProtectAPI coreProtectAPI;
    /**
     * The <code>LandsIntegration</code> instance, essentially the Lands API, being utilised within the
     * <code>CommandManager</code>. This will have been initialized <code>onEnable</code>.
     */
    private final LandsIntegration landsAPI;
    /**
     * An instance of the <code>Landlord</code> class.
     */
    private final Landlord landlordPlugin;


    /**
     * Constructor for the Manager, constructs the commandHashMap and initiates the various APIs and Landlord <code>Plugin</code>
     * instance.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance.
     * @param landsAPI The <code>LandsIntegration</code> instance, essentially the Lands API instance.
     * @param landlordPlugin The <code>Plugin</code> instance for the landlord Plugin.
     */
    public CommandManager(CoreProtectAPI coreProtectAPI, LandsIntegration landsAPI, Landlord landlordPlugin){
        this.coreProtectAPI =coreProtectAPI;
        this.landsAPI = landsAPI;
        this.landlordPlugin = landlordPlugin;
        this.subCommandHashMap = constructSubCommandHashMap();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        PlayerMessages playerMessages = new PlayerMessages(Bukkit.getPlayer(sender.getName()));
        if(!(sender instanceof Player)){
            Bukkit.getLogger().info("[Landlord] Commands cannot be execute from console. ");
            return false;
        }



        if(!subCommandHashMap.containsKey(args[0])){
            playerMessages.basicErrorMessage("Executed command does not exist!",
                                         "Only attempt execution of existing commands.");
            return false;
        }

        SubCommand subCommand = subCommandHashMap.get(args[0]);

        if(args.length > 2){
            String syntax = subCommand.getSyntax();

            playerMessages.basicErrorMessage("Too many arguements for the "+subCommand.getName()+" command."
                                       ,"The please follow the command syntax: "+syntax);
            return false;

        }
        subCommand.perform(sender,args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> suggestedTabs;
        if(args.length == 0){
            return null;
        }
        if(args.length==1){
            return subCommandHashMap.keySet().stream().toList();
        }

        if(args[0].equalsIgnoreCase("activitycheck")){
            suggestedTabs = new ArrayList<>();
            landsAPI.getLands().forEach(land-> suggestedTabs.add(land.getName()));
            return suggestedTabs;
        }
        return null;
    }

    /**
     * Constructs a <code>HashMap</code> consisting of <code>String</code> keys and child classes of <code>SubCommand</code>
     * abstract class.
     * @return Constructed <code>SubCommand HashMap</code>.
     */
    private HashMap<String, SubCommand> constructSubCommandHashMap(){
        HashMap<String,SubCommand> subCommandHashMap = new HashMap<>();
        subCommandHashMap.put("ownercheck",new LandOwnerLimitCheck(landlordPlugin,landsAPI,coreProtectAPI));
        subCommandHashMap.put("upkeeptop",new UpkeepChecker(landsAPI));
        subCommandHashMap.put("activitycheck",new IndividualLandActivityChecker(landlordPlugin,landsAPI,coreProtectAPI));
        return subCommandHashMap;
    }
}
