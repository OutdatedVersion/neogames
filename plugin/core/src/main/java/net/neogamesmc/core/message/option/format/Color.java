package net.neogamesmc.core.message.option.format;

import com.google.common.collect.EnumBiMap;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.core.message.option.MessageOption;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/20/2017 (2:03 PM)
 */
@AllArgsConstructor
public enum Color implements MessageOption
{

    /**
     * Represents black.
     */
    BLACK(ChatColor.BLACK),

    /**
     * Represents dark blue.
     */
    DARK_BLUE(ChatColor.DARK_BLUE),

    /**
     * Represents dark green.
     */
    DARK_GREEN(ChatColor.DARK_GREEN),

    /**
     * Represents dark blue (aqua).
     */
    DARK_AQUA(ChatColor.DARK_AQUA),

    /**
     * Represents dark red.
     */
    DARK_RED(ChatColor.DARK_RED),

    /**
     * Represents dark purple.
     */
    DARK_PURPLE(ChatColor.DARK_PURPLE),

    /**
     * Represents gold.
     */
    GOLD(ChatColor.GOLD),

    /**
     * Represents gray.
     */
    GRAY(ChatColor.GRAY),

    /**
     * Represents dark gray.
     */
    DARK_GRAY(ChatColor.DARK_GRAY),

    /**
     * Represents blue.
     */
    BLUE(ChatColor.BLUE),

    /**
     * Represents green.
     */
    GREEN(ChatColor.GREEN),

    /**
     * Represents aqua.
     */
    AQUA(ChatColor.AQUA),

    /**
     * Represents red.
     */
    RED(ChatColor.RED),

    /**
     * Represents light purple.
     */
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE),

    /**
     * Represents yellow.
     */
    YELLOW(ChatColor.YELLOW),

    /**
     * Represents white.
     */
    WHITE(ChatColor.WHITE);

    /**
     * Lookup map of our color representation to the BungeeCord API one.
     */
    private static final EnumBiMap<ChatColor, Color> RELATION;

    // populate
    static
    {
        RELATION = EnumBiMap.create(ChatColor.class, Color.class);

        for (ChatColor color : ChatColor.values())
            RELATION.put(color, Color.valueOf(color.name()));
    }

    /**
     * The actual color this is delegating.
     */
    private ChatColor ref;

    /**
     * Set the color for the currently selected element on the builder.
     *
     * @param builder The builder
     */
    @Override
    public void accept(ComponentBuilder builder)
    {
        builder.color(this.ref);
    }

    /**
     * Grab our color representation from the provided API one.
     *
     * @param color The color
     * @return Our color
     */
    public static Color from(ChatColor color)
    {
        return RELATION.get(color);
    }

}
