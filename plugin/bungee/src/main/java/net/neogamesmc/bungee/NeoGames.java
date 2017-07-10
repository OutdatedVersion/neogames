package net.neogamesmc.bungee;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.neogamesmc.bungee.communication.MessageProcessor;
import net.neogamesmc.bungee.dynamic.ServerCreator;
import net.neogamesmc.bungee.handle.ConnectionHandler;
import net.neogamesmc.bungee.handle.Ping;
import net.neogamesmc.bungee.handle.PunishmentProcessor;
import net.neogamesmc.bungee.queue.PlayerQueue;
import net.neogamesmc.bungee.tracking.PlayerTracking;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;

import java.util.concurrent.TimeUnit;

/**
 * BungeeCord plugin bootstrapping.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/26/2017 (3:36 PM)
 */
@Singleton
public class NeoGames extends Plugin
{

    /**
     * Runtime based dependency injector.
     */
    private Injector injector;

    @Override
    public void onEnable()
    {
        injector = Guice.createInjector(binder ->
        {
            binder.bind(ProxyServer.class).toInstance(ProxyServer.getInstance());
            binder.bind(NeoGames.class).toInstance(this);
        });

        injector.getInstance(RedisHandler.class).init().subscribe(RedisChannel.DEFAULT, RedisChannel.NETWORK);
        inject(Ping.class, PunishmentProcessor.class, ConnectionHandler.class, MessageProcessor.class, PlayerQueue.class, PlayerTracking.class);

        injector.getInstance(ServerCreator.class).createAndStartServer("lobby");

        getProxy().getScheduler().schedule(this, injector.getInstance(PlayerQueue.class), 0, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Create an instance of the provided classes.
     * <p>
     * If one of the classes is a {@link Listener}
     * then register it to the proxies' system.
     *
     * @param classes All of the classes
     */
    public void inject(Class... classes)
    {
        for (Class clazz : classes)
        {
            // noinspection unchecked
            Object obj = injector.getInstance(clazz);

            // auto-register listeners
            if (obj instanceof Listener)
                getProxy().getPluginManager().registerListener(this, (Listener) obj);
        }
    }

    /**
     * Run a task asynchronously.
     *
     * @param runnable The task to run
     */
    public void async(Runnable runnable)
    {
        ProxyServer.getInstance().getScheduler().runAsync(this, runnable);
    }

}
