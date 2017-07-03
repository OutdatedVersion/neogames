package net.neogamesmc.bungee.handle;

import com.google.inject.Inject;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.neogamesmc.bungee.NeoGames;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (2:38 AM)
 */
public class ProxyLoginHooks implements Listener
{

    /**
     * Local instance of our plugin.
     */
    @Inject private NeoGames plugin;

    @EventHandler ( priority = EventPriority.LOW )
    public void process(PreLoginEvent event)
    {
        event.registerIntent(plugin);



        event.completeIntent(plugin);
    }

}
