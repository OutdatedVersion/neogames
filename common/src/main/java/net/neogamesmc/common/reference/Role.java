package net.neogamesmc.common.reference;

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
    ADMIN(ChatColor.RED),
    MOD(ChatColor.DARK_GREEN),
    VIP(ChatColor.YELLOW),
    BUILDER(ChatColor.DARK_AQUA),
    // ^ staff

    YOUTUBE(ChatColor.RED),
    YT(ChatColor.DARK_RED),
    // ^ media

    PAL(ChatColor.YELLOW),
    FAM(ChatColor.DARK_AQUA),
    HOMIE(ChatColor.LIGHT_PURPLE),
    BRO(ChatColor.BLUE),
    DUDE(ChatColor.GREEN),
    // ^ donator

    DEFAULT(ChatColor.GRAY);

    /**
     * Common array holding every possible {@link Role}.
     */
    public static final Role[] VALUES = values();

    /**
     * Color of the role publicly displayed.
     */
    public ChatColor color;

    /**
     * The public facing name for this role.
     */
    public String name;

    /**
     * Constructor
     *
     * @param color See {@link #color}
     */
    Role(ChatColor color)
    {
        this.color = color;
        this.name = this.name().toLowerCase().replaceAll("_", "");
    }

    /**
     * Grab the display name for this role.
     * <p>
     * You must handle color resetting.
     *
     * @return The display
     */
    public String toName()
    {
        return this.color + "" + ChatColor.BOLD + this.name.toUpperCase();
    }

    /**
     * Check whether or not the provided role
     * is of a higher position than this one.
     *
     * @param other The other role
     * @return Yes or no
     */
    public boolean compare(Role other)
    {
        return this.ordinal() <= other.ordinal();
    }

}
