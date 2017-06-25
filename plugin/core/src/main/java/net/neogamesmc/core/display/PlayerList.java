package net.neogamesmc.core.display;

import com.google.inject.Inject;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Adds formatting to the player-list.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/19/2017 (2:41 AM)
 */
@ParallelStartup
public class PlayerList implements Listener
{

    /**
     * Local database instance.
     */
    @Inject private Database database;

    @EventHandler
    public void apply(PlayerLoginEvent event)
    {

    }

}
