package net.neogamesmc.bungee.handle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/27/2017 (3:20 PM)
 */
@Singleton
public class ConnectionHandler implements Listener
{

    /**
     * Our BungeeCord proxy instance.
     */
    @Inject private ProxyServer proxy;

    @EventHandler
    public void directLogin(ServerConnectEvent event)
    {
        // TODO(Ben): does not scale, need to switch out with network manager

        if (event.getTarget().getName().equals("Internal-Routing-Server"))
            event.setTarget(proxy.getServerInfo("lobby1"));
    }

}
