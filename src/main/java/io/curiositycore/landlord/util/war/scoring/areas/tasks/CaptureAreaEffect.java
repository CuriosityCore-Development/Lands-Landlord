package io.curiositycore.landlord.util.war.scoring.areas.tasks;

import io.curiositycore.landlord.util.war.participants.combatstats.teams.enums.TeamType;
import io.curiositycore.landlord.util.war.scoring.areas.enums.AreaInfluenceType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class CaptureAreaEffect extends BukkitRunnable {
    private final Location[] particleRadialLocations;
    private final Particle radiusParticle;
    private AreaInfluenceType areaInfluenceType;
    private final Particle.DustOptions particleOptionsAttacker = new Particle.DustOptions(Color.RED,1);;
    private final Particle.DustOptions particleOptionsDefender = new Particle.DustOptions(Color.BLUE,1);;
    private final Particle.DustOptions particleOptionsNeutral = new Particle.DustOptions(Color.ORANGE,1);

    public CaptureAreaEffect(Location sourceLocation, int numberOfParticles, int radius){
        this.radiusParticle = Particle.REDSTONE;
        this.particleRadialLocations = determineAreaRadiusPoints(numberOfParticles,sourceLocation,radius);
        this.areaInfluenceType = null;
    }

    private Location[] determineAreaRadiusPoints(int numberOfParticles,Location sourceLocation,int radius){
        Location[] radiusLocations = new Location[numberOfParticles];
        World worldOfArea = sourceLocation.getWorld();
        double angleIncrement = (2*Math.PI)/100;
        double centreYCoordinate = sourceLocation.getY();
        for(int index = 0 ; index < radiusLocations.length; index++){
            double angle = index*angleIncrement;
            double xCoordinate = sourceLocation.getX()+(radius*Math.cos(angle));
            double zCoordinate = sourceLocation.getZ()+(radius*Math.sin(angle));
            radiusLocations[index] = new Location(worldOfArea,xCoordinate,centreYCoordinate,zCoordinate);
        }
        return radiusLocations;
    }


    @Override
    public void run() {
        Particle.DustOptions particleOptions = areaRadiusColourCheck();

        for(Location particleLocation : this.particleRadialLocations){
            particleLocation.getWorld().spawnParticle(this.radiusParticle,particleLocation,1, particleOptions);
        }
    }

    public Particle.DustOptions areaRadiusColourCheck(){
        if(this.areaInfluenceType == null){
            return this.particleOptionsNeutral;
        }

        if(this.areaInfluenceType == AreaInfluenceType.ATTACKER_INFLUENCE){
            return this.particleOptionsAttacker;
        }

        return this.particleOptionsDefender;

    }

    public void setAreaInfluenceType(AreaInfluenceType areaInfluenceType) {
        this.areaInfluenceType = areaInfluenceType;
    }
}
