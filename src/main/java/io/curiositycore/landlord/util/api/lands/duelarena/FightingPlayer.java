package io.curiositycore.landlord.util.api.lands.duelarena;

import io.curiositycore.landlord.util.messages.MessageSender;
import org.bukkit.entity.Player;

/**
 * Represents a <code>Player</code> currently fighting within the arena.
 */
public class FightingPlayer {
    /**
     * The <code>Player</code> instance of the fighter.
     */
    private Player player;
    /**
     * A <code>PlayerMessages</code> instance for the fighting <code>Player</code>.
     */
    private MessageSender messageSender;
    /**
     * The current score of the fighter.
     */
    private int score;

    /**
     * Constructor that initialises both the <code>Player</code> instance of the fighter and a corresponding
     * <code>PlayerMessages</code> instance.
     * @param player
     */
    public FightingPlayer(Player player){
        this.player = player;
        this.messageSender = new MessageSender(player);
    }

    /**
     * Get the <code>Player</code> instance of the fighter.
     * @return The <code>Player</code> instance of the fighter.
     */
    public Player getPlayer() {
        return player;
    }
    /**
     * Get the <code>PlayerMessages</code> instance of the fighter.
     * @return The <code>PlayerMessages</code> instance of the fighter.
     */
    public MessageSender getPlayerMessages() {
        return messageSender;
    }
}
