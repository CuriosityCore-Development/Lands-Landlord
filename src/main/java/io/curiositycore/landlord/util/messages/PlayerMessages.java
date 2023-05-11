package io.curiositycore.landlord.util.messages;

import io.curiositycore.landlord.util.messages.enums.StandardChatComponents;
import io.curiositycore.landlord.util.messages.enums.HexChatColors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

/**
 * Provides methods to create and send different types of messages to a specified player.
 */
public class PlayerMessages {
    /**
     * Sets the <code>TextColor</code> of the Landlord <code>Plugin</code> titles to the
     * defined <code>HexChatColor</code>.
     */
    TextColor standardTitlePluginColor = HexChatColors.LANDLORD_STANDARD_TITLE_COLOR.getHexcode();
    /**
     * Sets the <code>TextColor</code> of the Landlord <code>Plugin</code> leaderboard header to the
     * defined <code>HexChatColor</code>.
     */
    TextColor standardLeaderboardHeaderColor = HexChatColors.LEADERBOARD_TITLE_COLOR.getHexcode();
    /**
     * Sets the <code>TextColor</code> of the Landlord <code>Plugin</code> general messages to the
     * defined <code>HexChatColor</code>.
     */
    TextColor standardTextColor = NamedTextColor.WHITE;

    /**
     * Instance of the <code>Player</code> whom the <code>Audience</code> of class messages are.
     */
    Player playerToMessage;

    /**
     * Constructor that sets the player to send messages to.
     * @param playerToMessage <code>Player</code> whom the focus of messages are.
     */
    public PlayerMessages(Player playerToMessage){
        this.playerToMessage = playerToMessage;
    }

    /**
     * Creates a title message for structured message bundles.
     * (E.g: For titling the receiving of data from a command.)
     *
     * @param messageFirstSegment First segment of the title
     * @param messageSecondSegment Second segment of the title
     */
    public void titleCreation(String messageFirstSegment, String messageSecondSegment){
        Component titleMessageStart = Component.text(messageFirstSegment).color(standardTitlePluginColor);

        Component titleMessageEnd = Component.text(messageSecondSegment).color(standardTextColor);

        Component compiledMessage = titleMessageStart.
                append(StandardChatComponents.SEPARATOR.component).
                append(titleMessageEnd);

        playerToMessage.sendMessage(compiledMessage);

    }

    /**
     * Creates a message to send to a basic plugin-standard message to a Player.
     * @param message The message to be sent to the Player.
     */
    public void basicPluginPlayerMessage(String message){
        Component messageComponent = Component.text(message).color(NamedTextColor.GRAY);
        Component compiledMessage = getLampBracketedHeader().
                append(messageComponent.asComponent());

        playerToMessage.sendMessage(compiledMessage);
    }

    /**
     * Creates an error message for the player, as per plugin-standard.
     * @param errorMessage The primary error message
     * @param clarificationMessage A clarificaiton of the error
     */
    public void basicErrorMessage(String errorMessage,String clarificationMessage){
        Component errorComponent = Component.text(errorMessage).color(NamedTextColor.RED);
        Component clarificationComponent = Component.text(clarificationMessage).color(NamedTextColor.GRAY);

        Component compiledMessage = getLampBracketedHeader().
                append(errorComponent.asComponent()).append(clarificationComponent);

        playerToMessage.sendMessage(compiledMessage);
    }
    /**
     * Creates the bracketed "[LAMP]" for certain messages
     * @return a text component with the String "[LAMP]"
     */
    private Component getLampBracketedHeader(){
        Component lampBracketedHeader = StandardChatComponents.BEGIN_SQUARE_BRACKET.component.
                append(StandardChatComponents.PLUGIN_TITLE.component).
                append(StandardChatComponents.END_SQUARE_BRACKET.component);

        return lampBracketedHeader;

    }
    /**
     * Creates a header message to use as a seperating line.
     * @param sectionHeaderTitle The header for the particular section that requires a header.
     */
    public void headerCreation(String sectionHeaderTitle){




        Component headerScanName = Component.text(sectionHeaderTitle+ " ").color(standardTitlePluginColor);

        Component headerMessageCompiled = StandardChatComponents.HEADER.component.
                append(StandardChatComponents.PLUGIN_TITLE.component).
                append(StandardChatComponents.SEPARATOR.component).
                append(headerScanName).append(StandardChatComponents.HEADER.component);

        playerToMessage.sendMessage(headerMessageCompiled);
    }
    /**
     * Creates a header message to use as a separating line for leaderboards.
     * @param sectionHeaderTitle The header for the particular leaderboard section header.
     */
    public void leaderboardHeaderCreation(String sectionHeaderTitle){

        Component headerScanName = Component.text(sectionHeaderTitle+ " ").color(standardLeaderboardHeaderColor);

        Component leaderboardMessageCompiled = StandardChatComponents.HEADER.component.
                append(StandardChatComponents.PLUGIN_TITLE.component.color(standardLeaderboardHeaderColor)).
                append(StandardChatComponents.SEPARATOR.component).
                append(headerScanName).append(StandardChatComponents.HEADER.component);

        playerToMessage.sendMessage(leaderboardMessageCompiled);
    }

    /**
     * Creates a message for a single subject of a leaderboard.
     * @param subjectRanking The <code>String</code> for the number ranking the subject has in the leaderboard.
     * @param scoreableSubject The <code>String</code> for the leaderboard subject's name.
     * @param score The <code>String</code> for the score the subject has within the leaderboard.
     */
    public void scoreboardMessage(String subjectRanking,String scoreableSubject, String score){
        Component scoreboardMessageComponent = Component.text(subjectRanking+". "+scoreableSubject+", "+score).color(standardTextColor);
        playerToMessage.sendMessage(scoreboardMessageComponent);
    }
}
