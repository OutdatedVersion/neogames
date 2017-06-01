package com.neomccreations.common.reference;

import net.md_5.bungee.api.ChatColor;

/**
 * Available in-game roles for players.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (10:06 PM)
 */
public enum Role
{

    OWNER(ChatColor.GOLD),
    DEV(ChatColor.DARK_PURPLE),
    ADMIN(ChatColor.DARK_PURPLE),
    MOD(ChatColor.DARK_GREEN),
    BUILDER(ChatColor.DARK_AQUA),
    // ^ staff

    YOUTUBE(ChatColor.RED),
    YT(ChatColor.DARK_PURPLE),
    // ^ media

    FAM(ChatColor.DARK_AQUA),
    HOMIE(ChatColor.LIGHT_PURPLE),
    DUDE(ChatColor.GREEN),
    BRO(ChatColor.YELLOW),
    // ^ donator

    DEFAULT(ChatColor.GRAY);

    /**
     * Color of the role publicly displayed.
     */
    public ChatColor color;

    Role(ChatColor color)
    {
        this.color = color;
    }

}
