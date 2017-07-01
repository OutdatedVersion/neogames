package net.neogamesmc.buycraft;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.md_5.bungee.api.plugin.Plugin;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.redis.RedisHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (1:16 AM)
 */
public class BuycraftHook extends Plugin
{

    /**
     * Dependency injection tool.
     */
    private Injector injector = Guice.createInjector();

    @Override
    public void onEnable()
    {
        get(RedisHandler.class).init();

        getProxy().getPluginManager().registerCommand(this, get(HookCommand.class));
        getProxy().getScheduler().schedule(this, get(TransactionProcessor.class), 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDisable()
    {
        get(Database.class).release();
        get(RedisHandler.class).release();
    }

    /**
     * Grab an instance of a class via our injector.
     *
     * @param clazz The class
     * @param <T> Type-parameter for this class.
     * @return The instance
     */
    public <T> T get(Class<T> clazz)
    {
        return injector.getInstance(clazz);
    }

}
