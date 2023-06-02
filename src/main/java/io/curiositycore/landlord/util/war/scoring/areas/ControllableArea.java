package io.curiositycore.landlord.util.war.scoring.areas;

import io.curiositycore.landlord.util.bars.AreaProgressBar;
import io.curiositycore.landlord.util.war.CustomWar;
import io.curiositycore.landlord.util.war.scoring.areas.enums.AreaInfluenceType;
import io.curiositycore.landlord.util.war.scoring.areas.tasks.AreaControlChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;

/**
 * An area within the lands of a <code>CustomWar</code> that can be captured by <code>Participant</code>s to
 * gain points.
 */
public class ControllableArea {
    /**
     * The <code>AreaControlChecker</code> instance for this area, used to do all the checks related to
     * <code>Participant</code> effects on the area during the <code>CustomWar</code>
     */
    AreaControlChecker areaControlChecker;

    /**
     * The <code>AreaProgressBar</code> linked to this area, used to define how much influence teams have over the area.
     */
    AreaProgressBar progressBar;
    //TODO when the area source object is made, reduce parameters to just the area source

    /**
     * Constructor that initializes the area's source, required radius and influence.
     * @param currentWar The <code>CustomWar</code> instance this area is connected to.
     * @param areaSourceLocation The <code>Location</code> of the source of this area.
     * @param areaRadius The maximum distance a <code>Participant</code> can be away from the
     *                  <code>areaSourceLocation</code> and still counted within the <code>areaControlChecker</code>
     * @param requiredInfluence The required amount of influence a team needs to capture the area.
     */
    public ControllableArea(CustomWar currentWar,
                            Location areaSourceLocation, int areaRadius, float requiredInfluence){


        this.areaControlChecker = new AreaControlChecker(currentWar,requiredInfluence,areaRadius,areaSourceLocation);

        this.progressBar = areaControlChecker.getAreaProgressBar();
    }
    //TODO This is now displaying the bars, but not updating properly, something to do with it being cancelled.

    /**
     * Shows the <code>BossBar</code> instances of the <code>AreaProgressBar</code> to every applicable
     * <code>Participant</code> of the <code>CustomWar</code> and begins the control checks for this area.
     */
    public void startCaptureChecks(){
        try{
            this.progressBar.activateBossBars();

        }
        catch(NullPointerException nullPointerException){
            String errorMessage = " is null, so bossbar activation was stopped at this point!";
            String nullObjectName = nullPointerException.getStackTrace()[0].toString();
            Bukkit.getLogger().warning(nullObjectName+" "+ errorMessage);

        }
        String name = Bukkit.getPluginManager().getPlugin("Landlord").getName();
        areaControlChecker.runTaskTimer(Bukkit.getPluginManager().getPlugin(name),20L,20L );

    }



}





