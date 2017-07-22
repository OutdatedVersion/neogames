package net.neogamesmc.core.bukkit;

import com.google.gson.Gson;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import lombok.SneakyThrows;
import lombok.val;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.payload.NotifyNetworkOfServerPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.core.command.api.CommandHandler;
import net.neogamesmc.core.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileReader;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (5:12 PM)
 */
public abstract class Plugin extends JavaPlugin implements Listener
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
    @SneakyThrows
    public <T> T get(Class<T> clazz)
    {
        return injector.getInstance(clazz);
    }

    /**
     * Grab an instance of the provided class and
     * if it's a {@link Listener} register it.
     *
     * @param clazz The class
     * @param <T> Type-parameter for that class
     * @return The fresh instance
     */
    @SneakyThrows
    public <T> T register(Class<T> clazz)
    {
        T obj = get(clazz);

        if (obj instanceof Listener)
            Bukkit.getPluginManager().registerEvents((Listener) obj, this);

        return obj;
    }

    /**
     * Unregister the provided listener.
     *
     * @param clazz The class
     * @param <T> Type-parameter for this item
     * @return Instance of that listener
     */
    public <T> T unregister(Class<T> clazz)
    {
        T obj = get(clazz);

        if (obj instanceof Listener)
            HandlerList.unregisterAll((Listener) obj);

        return obj;
    }

    /**
     * Load up modules in our core/commons.
     */
    public void loadCore()
    {
        System.out.println("[Core] Beginning class-path traversal and class injection");

        new FastClasspathScanner("net.neogamesmc")
                    .addClassLoader(getClassLoader())
                    .matchClassesWithAnnotation(ParallelStartup.class, this::register)
                    .scan();
    }

    /**
     * Setup our command handler and register core commands.
     */
    public void setupCommands()
    {
        setupCommands(true);
    }

    /**
     * Setup our command handler and optionally register core commands.
     *
     * @param registerCore Whether or not to register our core commands
     */
    public void setupCommands(boolean registerCore)
    {
        val handler = register(CommandHandler.class).addProviders(CommandHandler.DEFAULT_PROVIDERS);

        if (registerCore)
            handler.registerInPackage("net.neogamesmc.core");
    }

    /**
     * Register this class as a listener.
     */
    public void registerAsListener()
    {
        Bukkit.getPluginManager().registerEvents(this, this);
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

                binder.requestStaticInjection(Scheduler.class);

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
            ex.printStackTrace();
            System.out.println("========= Issue encountered whilst starting server -- shutting down.");

            Bukkit.shutdown();
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
