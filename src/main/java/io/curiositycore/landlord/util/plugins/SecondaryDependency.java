package io.curiositycore.landlord.util.plugins;

import io.curiositycore.landlord.Landlord;
import io.curiositycore.landlord.events.MobListeners;

// TODO Write the javadocs for this enum.
public enum SecondaryDependency {
    ELITE_MOBS("EliteMobs") {
        @Override
        public void registerPluginListeners(Landlord landlord) {
            landlord.getServer().getPluginManager().registerEvents(new MobListeners(landlord.getLandsAPI()),landlord);
        }

        @Override
        public void registerPluginCommands(Landlord landlord) {

        }
    };

    private final String pluginName;

    SecondaryDependency(String pluginName){
        this.pluginName = pluginName;
    }

    public String getName(){
        return this.pluginName;
    }
    public abstract void registerPluginListeners(Landlord landlord);

    public abstract void registerPluginCommands(Landlord landlord);
}
