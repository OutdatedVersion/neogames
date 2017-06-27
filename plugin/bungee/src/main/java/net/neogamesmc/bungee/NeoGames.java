package net.neogamesmc.bungee;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.neogamesmc.bungee.handle.Ping;

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
        injector = Guice.createInjector();

        inject(Ping.class);
    }

    /**
     * Create an instance of the provided classes.
     * <p>
     * If one of the classes is a {@link Listener}
     * then register it to the proxies' system.
     *
     * @param classes All of the classes
     * @return This plugin instance
     */
    public NeoGames inject(Class... classes)
    {
        for (Class clazz : classes)
        {
            // noinspection unchecked
            Object obj = injector.getInstance(clazz);

            // auto-register listeners
            if (obj instanceof Listener)
                getProxy().getPluginManager().registerListener(this, (Listener) obj);
        }

        return this;
    }

}
