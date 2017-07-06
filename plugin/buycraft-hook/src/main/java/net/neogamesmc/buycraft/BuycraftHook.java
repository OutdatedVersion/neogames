package net.neogamesmc.buycraft;

import com.google.inject.Injector;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.core.bukkit.Plugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (1:16 AM)
 */
public class BuycraftHook extends Plugin
{

    /**
     * Service to run primary task.
     */
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void enable(Injector injector)
    {
        getCommand("hook").setExecutor(get(HookCommand.class));
        service.scheduleAtFixedRate(get(TransactionProcessor.class), 0, 500, TimeUnit.MILLISECONDS);

        System.out.println("[Store Hook] Tracking transactions via /hook");
    }

    @Override
    public void disable()
    {
        get(Database.class).release();
    }

}
