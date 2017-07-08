package net.neogamesmc.core.bukkit;

import com.google.gson.Gson;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.val;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.common.payload.NotifyNetworkOfServerPayload;
import net.neogamesmc.common.redis.RedisHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileReader;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (5:12 PM)
 */
public abstract class Plugin extends JavaPlugin
{

    /**
     * Runtime dependency injection
     */
    private Injector injector;

    /**
     * Run the setup login for this plugin.
     *
     * @param injector Our injector
     */
    public abstract void enable(Injector injector);

    /**
     * Shutdown login for this plugin
     */
    public abstract void disable();

    /**
     * Setup our injector instance with special gear.
     * <p>
     * Override in child class to use.
     *
     * @param binder Supplier providing the binder
     *                 associated with this injector.
     */
    public void setupInjector(Binder binder)
    {

    }

    /**
     * Grab an instance of the provided class with
     * the injector backing everything.
     *
     * @param clazz The class to get
     * @param <T> Type-parameter for that class
     * @return The fresh instance
     */
    public <T> T get(Class<T> clazz)
    {
        return injector.getInstance(clazz);
    }

    @Override
    public void onEnable()
    {
        try (FileReader reader = new FileReader(ServerConfiguration.DATA_FILE))
        {
            val data = new Gson().fromJson(reader, ServerConfiguration.class);
            val redis = new RedisHandler().init();

            this.injector = Guice.createInjector(binder ->
            {
                binder.bind(Plugin.class).toInstance(this);
                binder.bind(JavaPlugin.class).toInstance(this);

                binder.bind(ServerConfiguration.class).toInstance(data);
                binder.bind(RedisHandler.class).toInstance(redis);

                setupInjector(binder);
            });

            enable(this.injector);

            System.out.println("[Local Configuration]: " + data.toString());

            // Add to network
            if (data.interactWithNetwork)
                new NotifyNetworkOfServerPayload(data.name, data.group, true).publish(redis);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Unable to startup server.", ex);
        }
    }

    @Override
    public void onDisable()
    {
        val redis = get(RedisHandler.class).init();
        val data = get(ServerConfiguration.class);

        // Remove from proxy now
        if (data.interactWithNetwork)
            new NotifyNetworkOfServerPayload(data.name, data.group, false).publish(redis);

        disable();
        redis.release();
    }

}
