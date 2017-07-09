package net.neogamesmc.game;

import net.neogamesmc.core.player.Players;
import org.bukkit.Bukkit;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/08/2017 (1:14 PM)
 */
public class CapacityWatchdog implements Runnable
{

    /**
     * How often to run this task.
     * <p>
     * The default is {@code 5 seconds}.
     */
    static final int INTERVAL = 60;

    /**
     * Whether or not we've already checked if someone has logged in.
     */
    private boolean alreadyChecked;

    /**
     * Check if no one is online.
     */
    @Override
    public void run()
    {
        if (Players.count() == 0)
        {
            if (alreadyChecked)
                Bukkit.shutdown();
            else
                alreadyChecked = true;
        }
        else if (!alreadyChecked)
            alreadyChecked = false;
    }

}
