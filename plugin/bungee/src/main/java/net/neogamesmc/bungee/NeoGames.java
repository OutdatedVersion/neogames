package net.neogamesmc.bungee;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.neogamesmc.bungee.communication.MessageHandler;
import net.neogamesmc.bungee.handle.ConnectionHandler;
import net.neogamesmc.bungee.handle.Ping;
import net.neogamesmc.bungee.handle.PunishmentProcessor;
import net.neogamesmc.common.payload.RequestServerCreationPayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;

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
        inject(Ping.class, PunishmentProcessor.class, ConnectionHandler.class, MessageHandler.class);

        injector.getInstance(MessageHandler.class).createServer(new RequestServerCreationPayload(null, "lobby", null));
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
