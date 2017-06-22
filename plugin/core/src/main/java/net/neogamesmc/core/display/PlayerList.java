package net.neogamesmc.core.display;

import com.google.inject.Inject;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import org.bukkit.event.Listener;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/19/2017 (2:41 AM)
 */
@ParallelStartup
public class PlayerList implements Listener
{

    @Inject private Database database;

}
