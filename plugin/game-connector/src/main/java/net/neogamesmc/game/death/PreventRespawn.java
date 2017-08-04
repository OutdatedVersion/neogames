package net.neogamesmc.game.death;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/02/2017 (11:04 PM)
 */
public class PreventRespawn implements Listener
{

    /**
     * Prevent players from actually receiving the respawn UI.
     *
     * @param event The event
     */
    @EventHandler
    public void handle(PlayerDeathEvent event)
    {
        event.getEntity().setHealth(20);
    }

}
