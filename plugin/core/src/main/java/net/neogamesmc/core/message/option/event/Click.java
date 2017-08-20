package net.neogamesmc.core.message.option.event;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.message.option.MessageOption;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.GRAY;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/20/2017 (2:56 PM)
 */
public class Click implements MessageOption
{

    /**
     * The actual action to perform.
     */
    private ClickEvent event;

    /**
     * Informative details provided context for the event.
     */
    private HoverEvent context;

    /**
     * Class Constructor
     *
     * @param action The action to perform
     * @param val Information for the primary event
     * @param details Text for the {@link #context}
     */
    private Click(ClickEvent.Action action, String val, BaseComponent[] details)
    {
        this.event = new ClickEvent(action, val);

        if (details != null)
            this.context = new HoverEvent(HoverEvent.Action.SHOW_TEXT, details);
    }

    /**
     * Create an event that will run the provided command.
     *
     * @param command The command to run
     * @return The click representation
     */
    public static Click command(String command)
    {
        return new Click(
                ClickEvent.Action.RUN_COMMAND,
                command,
                new ComponentBuilder("Click to run: ").color(GRAY).append(command).color(AQUA).bold(true).create()
        );
    }

    /**
     * Create an event that will suggest the provided command.
     *
     * @param command The suggestion
     * @return The click representation
     */
    public static Click commandSuggestion(String command)
    {
        return new Click(
                ClickEvent.Action.RUN_COMMAND,
                command,
                null
                // perhaps something like "Click to [insert,auto-type] <text>"
        );
    }

    /**
     * Create an event that will insert a hotlink for the provided web address.
     *
     * @param url The address
     * @return The click representation
     */
    public static Click url(String url)
    {
        return new Click(
                ClickEvent.Action.OPEN_URL,
                (url.startsWith("http") || url.startsWith("https")) ? url : "http://" + url,
                new ComponentBuilder("Click to visit: ").color(GRAY).append(Text.stripProtocol(url)).color(AQUA).bold(true).create()
        );
    }

    /**
     * Process this event representation using the provided builder.
     *
     * @param builder The builder
     */
    @Override
    public void accept(ComponentBuilder builder)
    {
        builder.event(event);

        if (context != null)
            builder.event(context);
    }

}
