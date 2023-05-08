package io.curiositycore.landlord.util.api.lands;

import me.angeschossen.lands.api.LandsIntegration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Utility class for any actions taken using the <code>landsAPI</code>.
 */
public class LandsApiInit {
    /**
     * Gets the API for the Lands <code>Plugin</code> after checking all prerequisites.
     * @param landlordPlugin The <code>Plugin</code> instance for the landlord Plugin.
     * @return The <code>LandsIntegration</code> instance, essentially the Lands API.
     */
    public static LandsIntegration apiGetter(Plugin landlordPlugin){
        LandsIntegration landsAPI = LandsIntegration.of(Bukkit.getServer().getPluginManager().getPlugin("landlord"));

        if(landsAPI == null){
            landlordPlugin.getLogger().severe("[Landlord] Failed to initialize the LandsAPI!");
            return null;
        }
        return landsAPI;
    }

    /**
     * Checks if the <code>LandsIntegration</code> instance <i>(landsAPI)</i> exists or not.
     * @param landsAPI The <code>LandsIntegration</code> instance, essentially the Lands API.
     * @return The <code>boolean</code> defining if the landsAPI exists or not.
     */
    private static boolean landsApiExists(LandsIntegration landsAPI){
        return (landsAPI == null);


    }
}
