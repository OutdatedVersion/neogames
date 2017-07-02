package net.neogamesmc.core.text;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.core.player.Players;
import org.bukkit.entity.Player;

import static java.lang.String.format;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (7:13 PM)
 */
public class Messages
{

    /**
     * Send a special message to every online player that is OP.
     *
     * @param content The content of the message to send.
     */
    public static void debug(Object content)
    {
        debug("Debug", content);
    }

    /**
     * Send a special message to every online
     * player that has OP.
     *
     * @param prefix The beginning part
     * @param content The content
     */
    public static void debug(String prefix, Object content)
    {
        final BaseComponent[] message = new ComponentBuilder("[" + prefix + "] ").color(ChatColor.YELLOW).bold(true)
                                                     .append(String.valueOf(content), NONE).create();

        Players.stream().filter(Player::isOp).forEach(player -> player.sendMessage(message));
        System.out.println(format("[Debug/%s] %s", prefix, content));
    }

}
