package net.neogamesmc.core.message.bar;

import lombok.val;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.neogamesmc.core.message.option.MessageOption;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/20/2017 (3:18 PM)
 */
public class Bar
{

    /**
     * Send the provided player a message displayed on their action bar.
     *
     * @param target The player
     * @param text The raw text
     * @param options Formatting options for the text
     */
    public static void send(Player target, String text, MessageOption... options)
    {
        val builder = new ComponentBuilder(text);

        for (MessageOption option : options)
            option.accept(builder);

        target.sendActionBar(TextComponent.toLegacyText(builder.create()));
    }

}
