package net.neogamesmc.bungee.handle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.neogamesmc.bungee.NeoGames;
import net.neogamesmc.bungee.distribution.DistributionMethod;
import net.neogamesmc.bungee.distribution.PlayerDirector;

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
    public static final BaseComponent[] DISALLOW_MESSAGE = new ComponentBuilder("Please be sure you're using Minecraft 1.11.1 or above!").color(ChatColor.YELLOW).create();

    /**
     * Our plugin instance.
     */
    @Inject private NeoGames plugin;

    /**
     * Send players certain places.
     */
    @Inject private PlayerDirector director;

    @EventHandler
    public void directLogin(ServerConnectEvent event)
    {
        if (event.getTarget().getName().equals("Internal-Routing-Server"))
            director.sendPlayer(event.getPlayer(), "lobby", DistributionMethod.ROUND_ROBBIN);
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

}
