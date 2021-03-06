package net.neogamesmc.bungee.handle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.payload.ModifyMOTDPayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;

import java.util.function.Consumer;

import static net.md_5.bungee.api.ChatColor.*;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;

/**
 * Respond to ping requests aimed at the proxy.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/26/2017 (3:38 PM)
 */
@Singleton
@ParallelStartup
public class Ping implements Listener
{

    /**
     * The first line of the response. Keep in mind that it will never change.
     */
    private static final ComponentBuilder FIRST_LINE = new ComponentBuilder
                                    ("                 ")
                             .append("!").color(AQUA).obfuscated(true)
                             .append(" NeoGames", NONE).color(ChatColor.GOLD).bold(true).append(" Network ").color(ChatColor.YELLOW)
                             .append("!\n", NONE).obfuscated(true).color(AQUA);

    /**
     * Name of the "protocol" object sent out by the proxy.
     */
    private static final String PROTOCOL_NAME = "NeoGames (1.11.2+)";

    /**
     * The top-bound to the amount of players
     * that may be online concurrently.
     */
    private volatile int maxPlayerCount = 500;

    /**
     * The text that is sent to clients sending
     * ping requests to the proxy.
     * <p>
     * It may be "hot-swapped" at any point.
     */
    private volatile BaseComponent responseText = updateResponse(ComponentBuilder::create);

    @Inject
    public Ping(RedisHandler redis)
    {
        redis.registerHook(this);
    }

    /**
     * Update the {@code message of the day} for this proxy.
     *
     * @param supply A way to add more text to the builder being used
     * @return This instance; for chaining
     */
    public BaseComponent updateResponse(Consumer<ComponentBuilder> supply)
    {
        final ComponentBuilder builder = new ComponentBuilder(FIRST_LINE);
        supply.accept(builder);

        return this.responseText = new TextComponent(builder.create());
    }

    /**
     * Update the max player count for this proxy.
     *
     * @param to The new count
     * @return This instance; for chaining
     */
    public Ping updatePlayerBound(int to)
    {
        this.maxPlayerCount = to;
        return this;
    }

    @EventHandler
    public void format(ProxyPingEvent event)
    {
        event.getResponse().setDescriptionComponent(responseText);
        event.getResponse().getPlayers().setMax(maxPlayerCount);
        event.getResponse().getVersion().setName(PROTOCOL_NAME);
    }

    /**
     * Expose a method to update ping response components via Redis.
     *
     * @param payload Payload containing the changes
     */
    @FromChannel ( RedisChannel.NETWORK )
    @HandlesType ( ModifyMOTDPayload.class )
    public void update(ModifyMOTDPayload payload)
    {
        // Update count
        if (payload.max != -1)
            updatePlayerBound(payload.max);


        // Update text
        // Lovely way of doing this, right?
        if (payload.line != null)
        {
            this.responseText = new TextComponent(
                    new TextComponent(FIRST_LINE.create()),
                    new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', payload.line.replaceAll("\\$", " "))))
            );
        }
    }

}
