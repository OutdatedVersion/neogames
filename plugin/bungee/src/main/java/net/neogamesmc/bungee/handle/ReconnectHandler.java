package net.neogamesmc.bungee.handle;

import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (9:31 PM)
 */
public class ReconnectHandler implements Listener
{

    public void handleKick(ServerKickEvent event)
    {
        System.out.println("Kick cause: " + event.getCause().name());
    }

}
