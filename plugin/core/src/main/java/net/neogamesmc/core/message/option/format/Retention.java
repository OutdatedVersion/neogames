package net.neogamesmc.core.message.option.format;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.core.message.option.MessageOption;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/20/2017 (2:21 PM)
 */
@AllArgsConstructor
public enum Retention implements MessageOption
{

    /**
     * The new component will be fresh; nothing carried over.
     */
    NOTHING(ComponentBuilder.FormatRetention.NONE),

    /**
     * Formatting will carry over.
     */
    FORMATTING(ComponentBuilder.FormatRetention.FORMATTING),

    /**
     * Only events will carry over.
     */
    EVENTS(ComponentBuilder.FormatRetention.EVENTS),

    /**
     * Every "option" will be passed on from the previous component.
     */
    EVERYTHING(ComponentBuilder.FormatRetention.ALL);

    /**
     * What this item is delegating.
     */
    public final ComponentBuilder.FormatRetention ref;

    /**
     * Apply the selected retention to the provided builder.
     *
     * @param builder The builder
     */
    @Override
    public void accept(ComponentBuilder builder)
    {
        builder.retain(ref);
    }

}
