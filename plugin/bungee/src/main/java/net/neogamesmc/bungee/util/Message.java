package net.neogamesmc.bungee.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/10/2017 (9:46 PM)
 */
public final class Message
{

    /**
     * Return a {@link ComponentBuilder} that already has our
     * message prefix appended to it.
     *
     * @param prefix The text to use as the prefix's content
     * @return The builder
     */
    public static ComponentBuilder prefix(String prefix)
    {
        return new ComponentBuilder(prefix + " » ").color(ChatColor.DARK_AQUA);
    }

}
