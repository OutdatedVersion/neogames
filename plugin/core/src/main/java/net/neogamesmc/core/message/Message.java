package net.neogamesmc.core.message;

import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.regex.Regex;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.message.option.MessageOption;
import net.neogamesmc.core.message.option.event.Click;
import net.neogamesmc.core.message.option.format.Color;
import net.neogamesmc.core.message.option.format.Retention;
import net.neogamesmc.core.player.Players;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static net.md_5.bungee.api.ChatColor.DARK_AQUA;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (7:45 PM)
 */
public class Message extends ComponentBuilder
{

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
        return content(text, Color.GRAY);
    }

    /**
     * Add something onto this message.
     *
     * @param content The text to add
     * @return This message builder
     */
    public Message content(Object content, MessageOption... optionsRaw)
    {
        val options = Arrays.asList(optionsRaw);
        val text = (options.contains(MessageOption.NO_LEADING_SPACE) ? "" : " ") + String.valueOf(content).trim();
        val isURL = Regex.URL.matcher(text).matches();

        // By default, we do not retain any formatting... follow through on that if there is no override provided..
        val retention = options.stream()
                               .filter(option -> option.getClass().equals(Retention.class))
                               .findFirst()
                               .map(option -> (Retention) option)
                               .orElse(Retention.NOTHING);


        // Add actual content to message
        append(isURL ? Text.stripProtocol(text) : text, retention.ref);

        // Apply options
        options.forEach(option -> option.accept(this));


        // Add clickable links; whilst respecting the option to skip over this
        if (!options.contains(MessageOption.DO_NOT_HOTLINK))
            if (isURL)
                Click.url(text).accept(this);

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
        return content(name, Color.GREEN);
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
