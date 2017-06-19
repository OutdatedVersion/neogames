package net.neomcgames.lobby;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.core.issue.Issues;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/19/2017 (3:30 AM)
 */
public class Lobby extends JavaPlugin
{

    /**
     * Runtime direct dependency injection.
     */
    private Injector injector;

    @Override
    public void onEnable()
    {
        // TODO(Ben): have custom plugin to handle injector creation and such

        injector = Guice.createInjector();

        new FastClasspathScanner(getClass().getPackage().getName())
                // start our default modules
                .matchClassesWithAnnotation(ParallelStartup.class, this::register)
                .scan();
    }

    /**
     *
     *
     * @param clazz The class to register
     */
    private <T> void register(final Class<T> clazz)
    {
        try
        {
            T obj = injector.getInstance(clazz);

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
