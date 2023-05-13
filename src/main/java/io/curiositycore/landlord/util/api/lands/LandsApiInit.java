package io.curiositycore.landlord.util.api.lands;

import me.angeschossen.lands.api.LandsIntegration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Utility class for any actions taken using the <code>landsAPI</code>.
 */
public class LandsApiInit {
    /**
     * Gets the API for the Lands <code>Plugin</code> after checking all prerequisites.
     * @param landlordPlugin The <code>Plugin</code> instance for the Landlord Plugin.
     * @return The <code>LandsIntegration</code> instance, essentially the Lands API.
     */
    public static LandsIntegration apiGetter(Plugin landlordPlugin){
        try {
            return LandsIntegration.of(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("landlord")));

        }
        catch (NullPointerException nullPointerException){
            landlordPlugin.getLogger().severe("[Landlord] Failed to initialize the LandsAPI!");
            return null;
        }

    }

}
