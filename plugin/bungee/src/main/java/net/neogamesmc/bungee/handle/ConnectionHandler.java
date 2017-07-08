package net.neogamesmc.bungee.handle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.neogamesmc.bungee.distribution.DistributionMethod;
import net.neogamesmc.bungee.distribution.PlayerDirector;

import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/27/2017 (3:20 PM)
 */
@Singleton
public class ConnectionHandler implements Listener
{

    /**
     * Disconnect players with this message if they're using
     * the wrong version of Minecraft.
     */
    private static final BaseComponent[] DISALLOW_MESSAGE = new ComponentBuilder("Please be sure you're using Minecraft 1.11.1 or above!").color(ChatColor.YELLOW).create();

    /**
     * Disconnect players with this message if no lobbies are online
     * to handle their login.
     */
    private static final BaseComponent[] NO_SERVERS_MESSAGE = new ComponentBuilder("Empty Pool.").color(ChatColor.YELLOW).bold(true)
            .append(" We have no online servers to serve your request.", NONE).color(ChatColor.RED).append(" :(").color(ChatColor.GRAY).create();

    /**
     * Send players certain places.
     */
    @Inject private PlayerDirector director;

    @EventHandler
    public void directLogin(ServerConnectEvent event)
    {
        if (event.getTarget().getName().equals("Internal-Routing-Server"))
        {
            val info = director.info("lobby", DistributionMethod.ROUND_ROBBIN);

            if (info != null)
                event.setTarget(info);
            else
                event.getPlayer().disconnect(NO_SERVERS_MESSAGE);
        }
    }

    @EventHandler ( priority = EventPriority.LOWEST )
    public void forceProtocol(PreLoginEvent event)
    {
        if (ProtocolConstants.MINECRAFT_1_11_1 > event.getConnection().getVersion())
        {
            event.setCancelled(true);
            event.setCancelReason(DISALLOW_MESSAGE);
        }
    }

    @EventHandler
    public void moveToLobbyOnServerStop(ServerKickEvent event)
    {
        System.out.println("ServerKickEvent | " + event.getCause().name() + " | " + event.getState().name());
    }

    @EventHandler
    public void disconnectEvent(ServerDisconnectEvent event)
    {
        System.out.println("ServerDisconnectEvent | " + event.getPlayer().getName() + " -> " + event.getTarget().getName());
    }

    @EventHandler
    public void playerDisconnectEvent(PlayerDisconnectEvent event)
    {
        System.out.println("PlayerDisconnectEvent | " + event.getPlayer().getName());
    }

    @EventHandler
    public void switchEvent(ServerSwitchEvent event)
    {
        System.out.println("ServerSwitchEvent | " + event.getPlayer().getName());
    }

    @EventHandler
    public void connectEvent(ServerConnectEvent event)
    {
        System.out.println("ServerConnectEvent | " + event.getPlayer().getName());
    }

    @EventHandler
    public void connectedEvent(ServerConnectedEvent event)
    {
        System.out.println("ServerConnectEvent | " + event.getPlayer().getName());
    }

}
