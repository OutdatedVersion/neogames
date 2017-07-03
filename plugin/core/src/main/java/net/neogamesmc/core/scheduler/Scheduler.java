package net.neogamesmc.core.scheduler;

import com.google.inject.Inject;
import net.neogamesmc.core.bukkit.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (9:32 AM)
 */
public class Scheduler
{

    /** Our plugin instance */
    @Inject private static Plugin plugin;

    /**
     * @return the one and only scheduler instance for this server
     */
    public static BukkitScheduler get()
    {
        return Bukkit.getScheduler();
    }

    /**
     * Stops the requested task instantly
     *
     * @param id the ID of the task
     *
     * @return the {@link BukkitScheduler} for this {@link org.bukkit.Server}
     */
    public static BukkitScheduler end(int id)
    {
        get().cancelTask(id);
        return get();
    }

    /**
     * Ends the task with the provided ID after
     * a certain amount of time. (measured in ticks)
     *
     * @param id         the ID of the task
     * @param ticksLater how many ticks later to run something
     *
     * @return the {@link BukkitScheduler} instance this server is using
     */
    public static BukkitScheduler endAfter(int id, long ticksLater)
    {
        delayed(() -> end(id), ticksLater);
        return get();
    }

    /**
     * Runs a task async to the main Minecraft thread.
     *
     * @param runnable The code to run
     * @return The Bukkit ID of the task
     */
    public static int async(Runnable runnable)
    {
        return get().runTaskAsynchronously(plugin, runnable).getTaskId();
    }

    /**
     * Runs a task after the desired amount of time.
     *
     * @param runnable The code to run
     * @param delay    The delay in ticks (20 ticks = 1 second)
     * @return the Bukkit ID for the task
     */
    public static int delayed(Runnable runnable, long delay)
    {
        return get().scheduleSyncDelayedTask(plugin, runnable, delay);
    }

    /**
     * Runs a task synchronous to the primary Minecraft thread.
     *
     * @param runnable The code to run
     * @return the Bukkit ID of the task
     */
    public static int sync(Runnable runnable)
    {
        return get().runTask(plugin, runnable).getTaskId();
    }

    /**
     * Run a task every {@code x} ticks.
     *
     * @param runnable The task
     * @param every The tick interval
     * @return The Bukkit task ID
     */
    public static int timer(Runnable runnable, long every)
    {
        return get().scheduleSyncRepeatingTask(plugin, runnable, 0, every);
    }

}
