package net.neogamesmc.core.message;

import net.md_5.bungee.api.ChatColor;

/**
 * Tools relating to working with Minecraft colors.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (7:54 AM)
 */
public class Colors
{

    /**
     * Returns a bold version of the provided color.
     *
     * @param color The color
     * @return Boldified version
     */
    public static String bold(ChatColor color)
    {
        return color + ChatColor.BOLD.toString();
    }

    /**
     * Colorize the provided text.
     *
     * @param in The raw text
     * @return The colorful text
     */
    public static String colorize(String in)
    {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

}
