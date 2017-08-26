package net.neogamesmc.bungee.limbo;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.ChannelFutureListener;
import lombok.Data;
import lombok.val;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Kick;
import net.neogamesmc.bungee.NeoGames;
import net.neogamesmc.bungee.limbo.netty.ChannelInitializer;
import net.neogamesmc.bungee.limbo.netty.LimboDownstream;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/24/2017 (4:00 PM)
 */
@Singleton
public class Limbo implements Listener, Runnable
{

    /**
     * Whilst processing kick packets we'll check if the message used starts with
     * this character sequence. If it does then we'll send them off to 'limbo'.
     * <p>
     * We keep it lowercase to avoid being case-sensitive.
     *
     * @see LimboDownstream#handle(Kick) Where this is used
     */
    public static final String REQ_SEND_TO_LIMBO = "neogames|send-to-limbo";

    /**
     *
     */
    public static final BaseComponent[] MESSAGE_IN_LIMBO = new ComponentBuilder("You are currently in limbo").color(ChatColor.DARK_AQUA).italic(true).create();

    /**
     *
     */
    public static final BaseComponent[] MESSAGE_ENTERING_LIMBO = new ComponentBuilder("You are now entering limbo").color(ChatColor.DARK_AQUA).italic(true).create();

    /**
     *
     */
    @Inject private ProxyServer proxy;

    /**
     *
     */
    private final NeoGames plugin;

    /**
     *
     */
    private Map<UUID, LimboPlayer> present = Maps.newConcurrentMap();

    @Inject
    public Limbo(NeoGames plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        for (LimboPlayer entry : present.values())
        {
            // let them know they're in limbo
            entry.connection.sendMessage(ChatMessageType.ACTION_BAR, MESSAGE_IN_LIMBO);

            // open up a connection and maintain it
            val initializer = new ChannelInitializer(entry.connection);

            ChannelFutureListener listener = future ->
            {
                if (future.isSuccess())
                {
                    //
                    future.channel().close();

                    entry.connection.unsafe().sendPacket(new KeepAlive(ThreadLocalRandom.current().nextInt()));

                    plugin.asyncDelayed(() ->
                    {

                    }, 5, TimeUnit.SECONDS);
                }
                else
                {
                    // issue?
                }
            };
        }
    }

    public void sendTo(UserConnection connection)
    {

    }

    public void removeFrom()
    {

    }

    @Data
    private static class LimboPlayer
    {
        private UserConnection connection;
        private long enteredAt;
        private long lastUpdatedAt;
        private boolean removeOnNextCycle;
    }

}
