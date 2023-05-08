package io.curiositycore.landlord.util.messages.enums;

import net.kyori.adventure.text.format.TextColor;
/**
 * Enum for defining the custom colors of the LandLord <code>Plugin</code>.
 */
public enum HexChatColors {
    /**
     * The standard color used for the Landlord <code>Plugin</code> titles.
     */
    LANDLORD_STANDARD_TITLE_COLOR("#00A800"),
    /**
     * The standard color used for the LandLord <code>Plugin</code> brackets.
     */
    BRACKET_COLOR("#545454");

    /**
     * The <code>TextColor</code> for the enum value.
     */
    final private TextColor hexCode;

    /**
     * Constructor for the custom color's hexcode.
     * @param hexCode the <code>String</code> for the custom color's hex code.
     */
    HexChatColors(String hexCode) {
        this.hexCode = TextColor.fromHexString(hexCode);
    }

    /**
     * Getter for the hexcode of the custom color.
     * @return the minecraft-formatted hexcode of the custom color.
     */
    public TextColor getHexcode(){
        return this.hexCode;

    }
}
