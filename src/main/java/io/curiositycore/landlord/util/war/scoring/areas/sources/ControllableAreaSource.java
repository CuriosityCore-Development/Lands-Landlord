package io.curiositycore.landlord.util.war.scoring.areas.sources;

import io.curiositycore.landlord.util.config.ConfigManager;
import io.curiositycore.landlord.util.config.settings.CustomWarSettings;
import io.curiositycore.landlord.util.war.CustomWar;
import io.curiositycore.landlord.util.war.scoring.areas.ControllableArea;
import org.bukkit.Location;

/**
 * Abstract to define the generalisation of the source of a <code>ControllableArea</code>.
 */
public abstract class ControllableAreaSource {
    /**
     * The <code>Location</code> of the source's central point.
     */
    protected Location sourceLocation;
    /**
     * The radius around the source in which a <code>Participant</code> counts towards capture.
     */
    protected int areaRadius;
    /**
     * The <code>ControllableArea</code> instance that this source creates.
     */
    protected ControllableArea controllableArea;
    /**
     * The amount of influence required to capture the <code>ControllableArea</code> of this source.
     */
    protected int influenceRequired;

    /**
     * Instance of the <code>ConfigManager</code> to be utilised within any <code>Listener</code> within this class.
     */
    protected ConfigManager configManager;

    /**
     * Constructor that initialises the config manager and the area /
     * @param configManager instance of the <code>ConfigManager</code>.
     * @param sourceLocation The <code>Location</code> source's central point.
     * @param currentWar The <code>CustomWar</code> instance this source belongs to.
     */
    public ControllableAreaSource(ConfigManager configManager,Location sourceLocation, CustomWar currentWar){

        this.configManager = configManager;
        this.influenceRequired = this.configManager.getInt(CustomWarSettings.AREA_INFLUENCE_REQUIREMENT.getPathArray());
        this.areaRadius = this.configManager.getInt(CustomWarSettings.AREA_CAPTURE_RADIUS.getPathArray());
        this.sourceLocation = sourceLocation;
        this.controllableArea = setControllableArea(currentWar);

    }

    /**
     * Initialises the timer for capturing the area this source creates.
     */
    public void startAreaTimer(){
        controllableArea.startCaptureChecks();
        this.broadcastAreaCapture();
    }

    /**
     * Abstract method defining the generalisation of setting the controllable area this source creates.
     * @param currentWar The <code>CustomWar</code> instance this source belongs to.
     * @return The <code>ControllableArea</code> of this source.
     */
    protected abstract ControllableArea setControllableArea(CustomWar currentWar);

    /**
     * Abstract method for defining the generalisation of broadcasting a message, to the applicable participants, of the
     * source block's area being captured.
     */
    protected abstract void broadcastAreaCapture();


}
