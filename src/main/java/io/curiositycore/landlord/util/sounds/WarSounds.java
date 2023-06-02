package io.curiositycore.landlord.util.sounds;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

/**
 * Enum defining the various <code>Sound</code> instances used in the <code>Plugin</code>
 */
public enum WarSounds {
    /**
     * Sound for when a <code>ControllableArea</code> is captured.
     */
    AREA_CAPTURED(Key.key("item.goat_horn.sound.1"), Sound.Source.AMBIENT);
    /**
     * The <code>Sound</code> to be utilised when conducting <code>Adventure</code> methods including the playing of
     * sounds in game.
     */
    private final Sound sound;

    /**
     * Constructor which initialises the <code>Sound</code> for each value.
     * @param key The key of the value's <code>Sound</code>
     * @param source The <code>Sound.Source</code> for which the sound will be played to the <code>Audience</code>
     */
    WarSounds(Key key, Sound.Source source){
        this.sound = Sound.sound(key,source,1f,1f);

    }

    /**
     * Getter for the <code>Sound</code>, usually got for the purposes of playing to an <code>Audience</code>.
     * @return The <code>Sound</code> of the value.
     */
    public Sound getSound(){
        return this.sound;
    }
}
