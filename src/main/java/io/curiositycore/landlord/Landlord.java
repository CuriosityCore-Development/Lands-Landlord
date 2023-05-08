package io.curiositycore.landlord;

import io.curiositycore.landlord.commands.CommandManager;
import io.curiositycore.landlord.events.OwnershipListeners;
import io.curiositycore.landlord.util.api.coreprotect.CoreprotectApiInit;
import io.curiositycore.landlord.util.api.lands.LandsApiInit;
import io.curiositycore.landlord.util.config.ConfigManager;
import io.curiositycore.landlord.util.maths.TimeConverter;
import io.curiositycore.landlord.util.tasks.ActivityCheck;
import me.angeschossen.lands.api.LandsIntegration;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of the Landlord <code>Plugin</code>. Landlord is an add-on to the Lands <code>Plugin</code> that
 * provides both automated scans and manual commands for ensuring that config-defined <code>Player</code> limits on
 * both their Activity in the server and the amount of <code>Land</code> claims they own. <br>
 * <b>This <code>Plugin</code> was made by CuriosityCore Development.</b>
 */
public final class Landlord extends JavaPlugin {
    /**
     * The <code>CoreProtectAPI</code> instance being utilised within this <code>Plugin</code>.
     */
    CoreProtectAPI coreProtectAPI;
    /**
     * The <code>CoreProtectAPI</code> instance being utilised within this <code>Plugin</code>.
     */
    LandsIntegration landsAPI;
    /**
     * Instance of the <code>ConfigManager</code> being utilised within <code>Plugin</code> initialization..
     */
    ConfigManager configManager;

    @Override
    public void onEnable() {
        this.configManager = configInitialisation();

        this.coreProtectAPI = getCoreProtectAPI();
        this.landsAPI = getLandsAPI();

        if(!isEnabled()){
            return;
        }

        initializeChecks();
        registerListeners();
        setRegisterCommandExecutors();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Getter for the <code>CoreProtectAPI</code>.<br> <i>(Disables the Plugin if null)</i>
     * @return The <code>CoreProtectAPI</code> instance.
     */
    private CoreProtectAPI getCoreProtectAPI(){
        CoreProtectAPI coreProtectAPI;

        coreProtectAPI = CoreprotectApiInit.getCoreProtect(this);

        if(coreProtectAPI == null) {
            getServer().getPluginManager().disablePlugin(this);

        }

        return coreProtectAPI;
    }

    /**
     * Getter for the instance of the <code>DefaultConfigManager</code> within this instance of the
     * Landlord <code>Plugin</code>.
     * @return The instance of the <code>DefaultConfigManager</code>.
     */
    public ConfigManager getDefaultConfigManager(){
        return this.configManager;
    }
    /**
     * Getter for the <code>LandsIntegration Instance</code> i.e. The API for the Lands <code>Plugin</code>. <br><i>(Disables the Plugin if null)</i>
     * @return The <code>LandsIntegration</code> instance.
     */
    private LandsIntegration getLandsAPI(){
        LandsIntegration landsAPI;
        landsAPI = LandsApiInit.apiGetter(this);

        if(landsAPI == null){
            getServer().getPluginManager().disablePlugin(this);
        }
        return landsAPI;
    }

    /**
     * Initializes the scheduled tasks for the landlord <code>Plugin</code>. <br><i>(Currently activity time and ownership)</i>
     */
    private void initializeChecks(){
        int delayInMinutes;
        int periodInHours;
        int delayInTicks;
        int periodInTicks;
        if(!configManager.getBoolean("activity_scan","enabled")){
            getLogger().info("[Landlord] Activity Scans not enabled in config. Scan Tasks not registered!");
        }
        delayInMinutes = configManager.getInt("activity_scan","delay_on_enable");
        periodInHours = configManager.getInt("activity_scan","scan_period");

        delayInTicks = TimeConverter.MINUTE.toTicks(delayInMinutes);
        periodInTicks = TimeConverter.HOUR.toTicks(periodInHours);

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new ActivityCheck(landsAPI,coreProtectAPI,this),delayInTicks,periodInTicks);
        getLogger().info("[Landlord] Activity Scan tasks registered!");
    }

    /**
     * Registers all listeners within the Landlord <code>Plugin</code>
     */
    private void registerListeners(){
        if(!ownershipLimitIsEnabled()){
            getLogger().info("[Landlord] Ownership limit not enabled in config. Listeners not registered!");
        }
        getServer().getPluginManager().registerEvents(new OwnershipListeners(this,landsAPI,coreProtectAPI),this);
        getLogger().info("[Landlord] Listeners successfully registered!");
    }

    /**
     * Initialises the config and gets an instance of the <code>DefaultConfigManager</code>
     * @return An instance of the <code>DefaultConfigManager</code>
     */
    private ConfigManager configInitialisation(){
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getLogger().info("[Landlord] Default Configuration File successfully initialised");
        return new ConfigManager(this);
    }

    /**
     * Checks to see if the <code>Land</code> ownership limit is enabled in the configuration file.
     * @return A <code>boolean</code> representing the result of the check.
     */
    private boolean ownershipLimitIsEnabled(){
        return (configManager.getBoolean("owner_limit","enabled"));
    }

    /**
     * Sets the executors for the commands of the Landlord <code>Plugin</code>.
     */
    private void setRegisterCommandExecutors(){
        getCommand("landlord").setExecutor(new CommandManager(coreProtectAPI,landsAPI,this));
        getLogger().info("[Landlord] Command executors successfully set!");
    }
}
