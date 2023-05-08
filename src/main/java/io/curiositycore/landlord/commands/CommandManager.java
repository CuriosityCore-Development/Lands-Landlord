package io.curiositycore.landlord.commands;

import io.curiositycore.landlord.Landlord;
import io.curiositycore.landlord.util.messages.PlayerMessages;
import me.angeschossen.lands.api.LandsIntegration;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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
    HashMap<String,SubCommand> subCommandHashMap;
    /**
     * The <code>CoreProtectAPI</code> instance being utilised within the <code>CommandManager</code>. This will have
     * been initialized <code>onEnable()</code>.
     */
    CoreProtectAPI coreProtectAPI;
    /**
     * The <code>LandsIntegration</code> instance, essentially the Lands API, being utilised within the
     * <code>CommandManager</code>. This will have been initialized <code>onEnable</code>.
     */
    LandsIntegration landsAPI;
    /**
     * An instance of the <code>Landlord</code> class.
     */
    Landlord landlordPlugin;
    /**
     * An instance of the <code>PlayerMessages</code> class, used to define any messages sent to a <code>Player</code>
     * executing this <code>Command</code>
     */

    PlayerMessages playerMessages;

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
        this.playerMessages = new PlayerMessages(Bukkit.getPlayer(sender.getName()));

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
        if(args.length==1){
            return suggestedTabs = subCommandHashMap.keySet().stream().toList();
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
        subCommandHashMap.put("ownerCheck",new LandOwnerLimitCheck(landlordPlugin,landsAPI,coreProtectAPI));
        return subCommandHashMap;
    }
}
