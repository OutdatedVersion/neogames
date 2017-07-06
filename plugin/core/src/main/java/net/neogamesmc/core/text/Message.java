package net.neogamesmc.core.text;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.core.player.Players;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (7:45 PM)
 */
public class Message
{

    /**
     * The default "missing permission" to send players.
     */
    public static Message PERMISSION_MESSAGE = prefix("Permissions").content("You are lacking the permission to do this.", ChatColor.RED);

    /**
     * The default message to send players if we couldn't run a command.
     */
    public static Message FAILED_TO_EXECUTE = prefix("Commands").content("Failed to execute command", ChatColor.RED);

    public static void noAccount(Player player, String provided)
    {
        prefix("Fetch").content("Failed to find player by name:", ChatColor.RED).content(provided, ChatColor.YELLOW).send(player);
    }

    /** The builder backing this message */
    private ComponentBuilder builder;

    /**
     * Whether or not we're currently working
     * with bold text.
     */
    private boolean isBold = false;

    /**
     * Whether or not we're currently working
     * with italicised text.
     */
    private boolean isItalic = false;

    /**
     * Class Constructor
     *
     * @param prefix The prefix
     */
    private Message(String prefix)
    {
        this.builder = new ComponentBuilder(prefix == null ? "" : (prefix + " »")).color(ChatColor.DARK_AQUA);
    }

    /**
     * @param prefix The starting content
     * @return A new message builder
     */
    public static Message prefix(String prefix)
    {
        return new Message(prefix);
    }

    /**
     * Create a new message builder.
     *
     * @return The fresh builder
     */
    public static Message start()
    {
        return new Message(null);
    }

    /**
     * Invert the current bold status on this message.
     *
     * @return This builder for chaining.
     */
    public Message bold()
    {
        builder.bold(isBold = !isBold);
        return this;
    }

    /**
     * Invert the current italics status on this message.
     *
     * @return This builder for chaining.
     */
    public Message italic()
    {
        builder.italic(isItalic = !isItalic);
        return this;
    }

    /**
     * Go to a new line on this message.
     *
     * @return This builder for chaining.
     */
    public Message newLine()
    {
        builder.append("\n");
        return this;
    }

    /**
     * @param text the text
     * @return this builder
     */
    public Message content(String text)
    {
        return content(text, ChatColor.GRAY);
    }

    /**
     * @param text the text to add
     * @param color color of the text
     * @return this builder
     */
    public Message content(String text, ChatColor color)
    {
        builder.append(" ").append(text).color(color);
        return this;
    }

    /**
     * Append a player's name to this builder.
     *
     * @param player The player itself
     * @return This builder
     */
    public Message player(Player player)
    {
        return player(player.getName());
    }

    /**
     * Append a player's name to this builder.
     *
     * @param name The name of this player
     * @return This builder
     */
    public Message player(String name)
    {
        return content(name, ChatColor.GREEN);
    }

    /**
     * @param player the player to send
     *               the message (at it's
     *               current state) to
     * @return the player
     */
    public Player send(Player player)
    {
        player.sendMessage(builder.append(".").color(ChatColor.GRAY).create());
        return player;
    }

    /**
     * @param player the player to send the message to
     * @return the player it was sent to
     */
    public Player sendAsIs(Player player)
    {
        player.sendMessage(builder.create());
        return player;
    }

    /**
     * Send this message to everyone online
     */
    public void send()
    {
        final BaseComponent[] message = builder.append(".").color(ChatColor.GRAY).create();

        Players.stream().forEach(player -> player.sendMessage(message));
    }

    /**
     * Send this message to everyone online.
     */
    public void sendAsIs()
    {
        final BaseComponent[] message = builder.create();
        Players.stream().forEach(player -> player.sendMessage(message));
    }

    /**
     * Grab the raw content behind this builder.
     *
     * @return The components to this message
     */
    public BaseComponent[] create()
    {
        return builder.create();
    }

}
