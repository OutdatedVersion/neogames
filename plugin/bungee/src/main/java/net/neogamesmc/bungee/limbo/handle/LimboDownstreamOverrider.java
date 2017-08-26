package net.neogamesmc.bungee.limbo.handle;

import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.event.EventHandler;
import net.neogamesmc.bungee.limbo.netty.LimboDownstream;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/24/2017 (4:40 PM)
 */
public class LimboDownstreamOverrider implements Listener
{

    /**
     * Replace the default {@link DownstreamBridge} with our own implementation. ({@link LimboDownstream})
     *
     * @param event The event
     */
    @EventHandler
    public void takeOverSwitch(ServerSwitchEvent event)
    {

    }

}
