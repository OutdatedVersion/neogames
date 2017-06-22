package net.neogamesmc.lobby;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.reference.Code;
import net.neogamesmc.core.issue.Issues;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Startup function(s) for a main lobby.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/19/2017 (3:30 AM)
 */
public class Lobby extends JavaPlugin
{

    /**
     * Runtime based direct dependency injection.
     */
    private Injector injector;

    @Override
    public void onEnable()
    {
        // TODO(Ben): have custom plugin to handle injector creation and such

        injector = Guice.createInjector();

        new FastClasspathScanner(Code.CORE_PACKAGE)
                // start our default modules
                .matchClassesWithAnnotation(ParallelStartup.class, this::register)
                .scan();
    }

    /**
     * Grab's an instance of the desired class
     * from our central injector.
     *
     * @param clazz The class
     * @param <T> Type of the class
     * @return An instance of {@code clazz}
     */
    public <T> T get(Class<T> clazz)
    {
        return injector.getInstance(clazz);
    }

    @Override
    public void onDisable()
    {
        get(Database.class).release();
    }

    /**
     * Creates an instance of the provided class.
     * <p>
     * If it happens to be a descendant of a {@link Listener}
     * we'll automatically register it with Bukkit as well.
     *
     * @param clazz The class to register
     */
    public <T> void register(final Class<T> clazz)
    {
        try
        {
            T obj = get(clazz);

            // auto-register event listeners
            if (obj instanceof Listener)
                getServer().getPluginManager().registerEvents((Listener) obj, this);
        }
        catch (Exception ex)
        {
            Issues.handle("Class Registration", ex);
        }
    }

}
