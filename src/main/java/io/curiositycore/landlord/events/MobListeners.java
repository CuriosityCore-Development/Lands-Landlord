package io.curiositycore.landlord.events;

import com.magmaguy.elitemobs.api.EliteMobSpawnEvent;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.enums.FlagModule;
import me.angeschossen.lands.api.flags.enums.RoleFlagCategory;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.flags.type.NaturalFlag;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MobListeners implements Listener {
    LandsIntegration landsAPI;
    public MobListeners(LandsIntegration landsAPI){
        this.landsAPI = landsAPI;
    }

    @EventHandler
    public void OnEliteMobSpawn(EliteMobSpawnEvent eliteMobSpawnEvent){
        Location spawnLocation = eliteMobSpawnEvent.getEliteMobEntity().getLocation();
        Chunk spawnChunk = spawnLocation.getChunk();

        try{
            Land landSpawnedIn = landsAPI.getLandByChunk(spawnLocation.getWorld(), spawnChunk.getX(), spawnChunk.getZ());
            if(!landSpawnedIn.getDefaultArea().hasNaturalFlag(Flags.MONSTER_SPAWN)){
                eliteMobSpawnEvent.setCancelled(true);
            }

        }
        catch(NullPointerException nullPointerException){

        }
    }

}
