package net.neogamesmc.core.message.option;

import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.function.Consumer;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/20/2017 (2:00 PM)
 */
public interface MessageOption extends Consumer<ComponentBuilder>
{

    /**
     * Indicates that the message should not automatically scan a message for web addresses
     * and insert a handler to have them be openable.
     */
    MessageOption DO_NOT_HOTLINK = builder -> { };

    /**
     * Let us know that the provided message should <strong>not</strong> have the auto-inserted space.
     */
    MessageOption NO_LEADING_SPACE = builder -> { };

}

// content("Hello", Color.RED)
// content("Hello", Color.RED, Format.UNDERLINE, Action.hover())