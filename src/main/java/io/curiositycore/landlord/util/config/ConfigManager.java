package io.curiositycore.landlord.util.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manager for the configuration file for the Landlord <code>Plugin</code>.
 */
public class ConfigManager {
    /**
     * The <code>FileConfiguration</code> instance of the Configuration file for the Landlord <code>Plugin</code>.
     */
    FileConfiguration config;

    /**
     * Constructor that utilises an instance of the Landlord <code>Plugin</code> to create a
     * <code>FileConfiguration</code> instance for the default config of the <code>Plugin</code>.
     * @param landlordPlugin An instance of the <code>Landlord</code> class.
     */
    public ConfigManager(JavaPlugin landlordPlugin){
        this.config = landlordPlugin.getConfig();
    }

    /**
     * Getter for an <code>int</code> value from within a configuration section option.
     * @param sectionPath A <code>String</code> representing the path to the Configuration Section.
     * @param optionName A <code>String</code> representing the name of the option to get from the
     * Configuration Section.
     *
     * @return The <code>int</code> value from within a configuration section option.
     */
    public int getInt(String sectionPath , String optionName){
        ConfigurationSection section = getConfigSection(sectionPath);
        return section.getInt(optionName);
    }
    /**
     * Getter for an <code>boolean</code> value from within a configuration section option.
     * @param sectionPath A <code>String</code> representing the path to the Configuration Section.
     * @param optionName A <code>String</code> representing the name of the option to get from the
     * Configuration Section.
     *
     * @return The <code>boolean</code> value from within a configuration section option.
     */
    public boolean getBoolean(String sectionPath , String optionName){
        ConfigurationSection section = getConfigSection(sectionPath);
        return section.getBoolean(optionName);
    }


    /**
     * Getter for the <code>ConfigurationSection</code> to be utilised by the configuration getter method that called it.
     * @param sectionPath A <code>String</code> representing the path to the Configuration Section.
     * @return the <code>ConfigurationSection</code> to be utilised by the configuration getter method that called it.
     */
    private ConfigurationSection getConfigSection(String sectionPath){
        return config.getConfigurationSection(sectionPath);
    }
}
