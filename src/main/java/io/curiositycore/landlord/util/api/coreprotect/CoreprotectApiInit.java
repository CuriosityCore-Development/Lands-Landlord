package io.curiositycore.landlord.util.api.coreprotect;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.plugin.Plugin;

/**
 * Utility class for any actions taken using the <code>CoreProtectAPI</code>
 */
public class CoreprotectApiInit {
    /**
     * Gets the CoreProtectAPI after checking all prerequisites of both the <code>CoreProtect Plugin</code> and the <code>CoreProtect API</code>.
     * @param landlordPlugin The <code>Plugin</code> instance for the landlord Plugin.
     * @return The <code>CoreProtectAPI</code> instance.
     */
    public static CoreProtectAPI getCoreProtect(Plugin landlordPlugin){
        Plugin coreprotectPlugin = landlordPlugin.getServer().getPluginManager().getPlugin("coreProtect");
        if(!pluginExists(coreprotectPlugin)){
            landlordPlugin.getLogger().severe("[Landlord] Failed to hook CoreProtect!");
            return null;
        }

        CoreProtectAPI coreProtectAPI = ((CoreProtect) coreprotectPlugin).getAPI();

        if(coreProtectAPI == null){
            landlordPlugin.getLogger().severe("[Landlord] Failed to initialize the CoreProtectAPI!");
            return null;
        }
        landlordPlugin.getLogger().severe("[Landlord] CoreProtectAPI initialized!");
        return coreProtectAPI;
    }

    /**
     * Checks if the CoreProtect <code>Plugin</code> exists within the server files.
     * @param coreprotectPlugin The CoreProtect <code>Plugin</code> instance.
     * @return The <code>boolean</code> instance defining if the existence of the CoreProtect <code>Plugin</code>.
     */
    private static boolean pluginExists(Plugin coreprotectPlugin) {
        if(coreprotectPlugin == null){
            return false;}
       return true;
    }

    /**
     * Checks if the <code>CoreProtectAPI</code> meets all prerequisites.
     * @param coreProtectAPI The <code>CoreProtectAPI</code> instance being checked.
     * @return The <code>boolean</code> instance defining if the prerequisites of the <code>CoreProtectAPI</code> have
     * been met.
     */
    private static boolean pluginApiExists(CoreProtectAPI coreProtectAPI) {
        if(!coreProtectAPI.isEnabled()){
            return false;
        }

        if(coreProtectAPI.APIVersion() < 6){
            return (coreProtectAPI.APIVersion() < 6);
        }
        return true;
    }
}
