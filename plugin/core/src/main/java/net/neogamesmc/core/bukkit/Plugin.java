package net.neogamesmc.core.bukkit;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

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
        this.injector = Guice.createInjector(binder ->
        {
            binder.bind(Plugin.class).toInstance(this);
            binder.bind(JavaPlugin.class).toInstance(this);

            setupInjector(binder);
        });

        enable(this.injector);
    }

    @Override
    public void onDisable()
    {
        disable();
    }

}
