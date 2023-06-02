package io.curiositycore.landlord.util.messages.enums;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * Enum for defining common phrases and components within plugin messages for ease of message creation.
 */
public enum StandardChatComponents {
    //The name of the plugin is Lands Alt Military Plugin (LAMP)
    /**
     * The <code>String</code> and <code>TextColor</code> used for the Landlord <code>Plugin</code> title.
     */
    PLUGIN_TITLE("Landlord", HexChatColors.LANDLORD_STANDARD_TITLE_COLOR.getHexcode()),
    /**
     * The <code>String</code> and <code>TextColor</code> used for the Landlord <code>Plugin</code> initial square bracket.
     */
    BEGIN_SQUARE_BRACKET("[", HexChatColors.BRACKET_COLOR.getHexcode()),
    /**
     * The <code>String</code> and <code>TextColor</code> used for the Landlord <code>Plugin</code> concluding square bracket.
     */
    END_SQUARE_BRACKET("] ", HexChatColors.BRACKET_COLOR.getHexcode()),
    /**
     * The <code>String</code> and <code>TextColor</code> used for the Landlord <code>Plugin</code> header lines.<br>
     * <i>(To be used with commands that require sectioning for readability purposes.)</i>
     */
    HEADER("----- ", NamedTextColor.WHITE),
    /**
     * The <code>String</code> and <code>TextColor</code> used for the Landlord <code>Plugin</code> seperator lines.<br>
     * <i>(Potential use in future for fancy header titles / sub-titles)</i>
     */
    SEPARATOR(" | ", NamedTextColor.WHITE),
    /**
     * The <code>String</code> and <code>TextColor</code> used for the Landlord <code>Plugin</code> message breaks. <br>
     * <i>(To be used with commands that require separation of <code>Key</code> and <code>Value</code>)</i>
     */
    MESSAGE_BREAK(" - ", NamedTextColor.WHITE);
    /**
     * A <code>Component</code> from the <code>adventure</code> api.
     */
    public final Component component;

    /**
     * Method for constructing the chat components for each enum.
     *
     * @param message The message to be associated with the enum
     * @param color   The color of the message to be assocsiated with the enum.
     */
    StandardChatComponents(String message, TextColor color) {
        this.component = Component.text(message).color(color);

    }
}
