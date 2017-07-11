package net.neogamesmc.core.text;

import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.neogamesmc.common.regex.Regex;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.player.Players;
import org.bukkit.entity.Player;

import java.util.function.Function;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (7:45 PM)
 */
public class Message extends ComponentBuilder
{

    /**
     * The default "missing permission" to send players.
     */
    public static Message PERMISSION_MESSAGE = prefix("Permissions").content("You are lacking the permission to do this.", ChatColor.RED);

    /**
     * The default message to send players if we couldn't run a command.
     */
    public static Message FAILED_TO_EXECUTE = prefix("Commands").content("Failed to execute command", ChatColor.RED);

    /**
     * Send a message to the provided player indicating we couldn't find an account by the provided name.
     *
     * @param player Message target
     * @param provided The text (name) inputted
     */
    public static void noAccount(Player player, String provided)
    {
        prefix("Fetch").content("Failed to find player by name:", ChatColor.RED).content(provided, ChatColor.YELLOW).send(player);
    }

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
     * @param prefix The prefix
     */
    private Message(String prefix)
    {
        super(prefix == null ? "" : (prefix + " »"));

        if (prefix != null)
            this.color(DARK_AQUA);
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
     * Go to a new line on this message.
     *
     * @return This builder for chaining.
     */
    public Message newLine()
    {
        append("\n");
        return this;
    }

    /**
     * @param text the text
     * @return this builder
     */
    public Message content(Object text)
    {
        return content(text, ChatColor.GRAY, null);
    }

    /**
     * @param text The text
     * @param color The color
     * @return this builder
     */
    public Message content(Object text, ChatColor color)
    {
        return content(text, color, null);
    }

    /**
     * Add something onto this message.
     *
     * @param content The text to add
     * @param color Color of the text
     * @param textFormatter Function to format text
     * @return This message builder
     */
    public Message content(Object content, ChatColor color, Function<String, String> textFormatter)
    {
        val text = " " + String.valueOf(content);

        // Add actual content to message
        append(textFormatter != null ? textFormatter.apply(text) : text, FormatRetention.NONE).color(color);

        // Add clickable links
        if (Regex.URL.matcher(text.trim()).matches())
        {
            this.event(new ClickEvent(ClickEvent.Action.OPEN_URL, text.trim()));
            this.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to visit: ").color(GRAY).append(Text.stripProtocol(text)).color(AQUA).bold(true).create()));
        }

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
     * Set the previous component to the provided color.
     *
     * @param color The color
     * @return This message
     */
    @Override
    public Message color(ChatColor color)
    {
        super.color(color);
        return this;
    }

    /**
     * Set the previously added component to be bold.
     *
     * @param bold Bold or not
     * @return The message
     */
    @Override
    public Message bold(boolean bold)
    {
        super.bold(bold);
        return this;
    }

    /**
     * Set the previously added component to be italic.
     *
     * @param italic Italic or not
     * @return The message
     */
    @Override
    public Message italic(boolean italic)
    {
        super.italic(italic);
        return this;
    }

    /**
     * Set the previously added component to be underlined.
     *
     * @param underlined Underlined or not
     * @return The message
     */
    @Override
    public Message underlined(boolean underlined)
    {
        super.underlined(underlined);
        return this;
    }

    /**
     * Set the previously added component to be strikethrough.
     *
     * @param strikethrough strikethrough or not
     * @return The message
     */
    @Override
    public Message strikethrough(boolean strikethrough)
    {
        super.strikethrough(strikethrough);
        return this;
    }

    /**
     * Set the previous component to be obfuscated.
     *
     * @param obfuscated Obfuscation
     * @return This message
     */
    @Override
    public Message obfuscated(boolean obfuscated)
    {
        super.obfuscated(obfuscated);
        return this;
    }

    /**
     * @param player the player to send
     *               the message (at it's
     *               current state) to
     * @return the player
     */
    public Player send(Player player)
    {
        player.sendMessage(this.append(".").color(ChatColor.GRAY).create());
        return player;
    }

    /**
     * @param player the player to send the message to
     * @return the player it was sent to
     */
    public Player sendAsIs(Player player)
    {
        player.sendMessage(this.create());
        return player;
    }

    /**
     * Send this message to everyone online
     */
    public void send()
    {
        val message = this.append(".").color(ChatColor.GRAY).create();

        Players.stream().forEach(player -> player.sendMessage(message));
    }

    /**
     * Send this message to everyone online.
     */
    public void sendAsIs()
    {
        val message = this.create();
        Players.stream().forEach(player -> player.sendMessage(message));
    }

}
