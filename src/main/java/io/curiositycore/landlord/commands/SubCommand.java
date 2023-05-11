package io.curiositycore.landlord.commands;

import io.curiositycore.landlord.util.messages.PlayerMessages;
import org.bukkit.command.CommandSender;

/**
 * Abstract to define the generalisation of a sub-command within a primary <code>Plugin</code> command.
 */
public abstract class SubCommand {
    /**
     * The name of the <code>SubCommand</code>, as a <code>String</code>.
     */
    protected String name;
    /**
     * The detailed description of the <code>SubCommand</code> functionality, as a <code>String</code>.
     */
    protected String description;
    /**
     * The syntax to use when executing a command that includes this <code>SubCommand</code>, as a <code>String</code>.
     */
    protected String syntax;

    /**
     * Abstract to represent the generalisation of the getter for the name of a sub-command
     * made abstract as there may be other conditions that determine name of the sub-command,
     * other than a constant per sub-command.
     * @return the name of the sub-command.
     */

    public abstract String getName();

    /**
     * Abstract to represent the generalisation of the getter for the description of a sub-command
     * (made abstract as there may be other conditions that determine description of the sub-command,
     * other than a constant per sub-command).
     * @return the description of what the sub-command does.
     */
    public abstract String getDescription();

    /**
     * Abstract to represent the generalisation of the getter for the syntax of a sub-command
     * (made abstract as there may be other condition that determine syntax of the sub-command,
     * other than a constant per sub-command).
     * @return the syntax of how to write the sub-command.
     */
    public abstract String getSyntax();

    //TODO it might be worth adding player messages to this abstract class
    /**
     * Abstract method to represent the generalisation for what function the sub-command has when
     * executed by a player.
     * @param player The <code>CommandSender</code> of the <code>SubCommand</code>
     * @param arguments The arguments inputted by the <code>CommandSender</code>.
     */
    public abstract void perform(CommandSender player, String[] arguments);

}
