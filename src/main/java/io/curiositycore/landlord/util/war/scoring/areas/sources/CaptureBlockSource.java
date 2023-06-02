package io.curiositycore.landlord.util.war.scoring.areas.sources;

import io.curiositycore.landlord.util.config.ConfigManager;
import io.curiositycore.landlord.util.war.CustomWar;
import io.curiositycore.landlord.util.war.scoring.areas.ControllableArea;
import org.bukkit.Location;

/**
 * Defines the source of a <code>ControllableArea</code> of a Capture Block.
 */
public class CaptureBlockSource extends ControllableAreaSource {
    /**
     * Constructor that initialises the parent class fields.
     * @param configManager the <code>ConfigManager</code> instance for the <code>Plugin</code>.
     * @param sourceLocation the <code>Location</code> of the placed Capture Block
     * @param currentWar the <code>CurrentWar</code> instance that the Capture Block is tied to.
     */
    public CaptureBlockSource(ConfigManager configManager, Location sourceLocation, CustomWar currentWar) {
        super(configManager, sourceLocation, currentWar);
    }

    @Override
    protected ControllableArea setControllableArea(CustomWar currentWar) {

        return new ControllableArea(currentWar,
                this.sourceLocation,
                this.areaRadius,
                this.influenceRequired);
    }

    @Override
    protected void broadcastAreaCapture() {

    }


}
