package net.neogamesmc.core.message.option.format;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.core.message.option.MessageOption;

import java.util.function.Consumer;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/20/2017 (2:13 PM)
 */
@AllArgsConstructor
public enum Style implements MessageOption
{

    /**
     * Represents magical characters that change around randomly.
     */
    MAGIC(builder -> builder.obfuscated(true)),

    /**
     * Makes the text bold.
     */
    BOLD(builder -> builder.bold(true)),

    /**
     * Makes a line appear through the text.
     */
    STRIKETHROUGH(builder -> builder.strikethrough(true)),

    /**
     * Makes the text appear underlined.
     */
    UNDERLINE(builder -> builder.underlined(true)),

    /**
     * Makes the text italic.
     */
    ITALIC(builder -> builder.italic(true)),

    /**
     * Resets all previous chat colors or formats.
     */
    RESET(ComponentBuilder::reset);

    /**
     * What this item is delegating.
     */
    private Consumer<ComponentBuilder> ref;

    /**
     * Process this formatting option on the provided builder.
     *
     * @param builder The builder
     */
    @Override
    public void accept(ComponentBuilder builder)
    {
        ref.accept(builder);
    }

}
