package net.neogamesmc.lobby;

import com.google.inject.Injector;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.command.api.CommandHandler;
import net.neogamesmc.core.issue.Issues;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Startup function(s) for a main lobby.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/19/2017 (3:30 AM)
 */
public class Lobby extends Plugin implements Listener
{

    // small magma cube : blast off
    // green villager : chunk runner
    // skeleton w/ bow : bow plinko

    /**
     * Where players are sent when joining.
     */
    private Location spawnLocation;

    /**
     * Local instance
     */
    private Database database;

    @Override
    public void enable(Injector injector)
    {
        // Forcefully start database first
        this.database = register(Database.class);

        register(RedisHandler.class).init().subscribe(RedisChannel.DEFAULT);
        register(CommandHandler.class).addProviders(CommandHandler.DEFAULT_PROVIDERS)
                                      .registerInPackage("net.neogamesmc.core");

        System.out.println("Beginning class-path scan..");

        new FastClasspathScanner("net.neogamesmc")
                .addClassLoader(getClassLoader())
                .matchClassesWithAnnotation(ParallelStartup.class, this::register)
                .scan();

        getServer().getPluginManager().registerEvents(this, this);

        this.spawnLocation = new Location(Bukkit.getWorld("lobby"), 10.5, 64.5, 5.5, 177.4f, -12.4f);
    }

    @Override
    public void disable()
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
    public <T> T register(final Class<T> clazz)
    {
        try
        {
            T obj = get(clazz);

            // auto-register event listeners
            if (obj instanceof Listener)
                getServer().getPluginManager().registerEvents((Listener) obj, this);

            return obj;
        }
        catch (Exception ex)
        {
            Issues.handle("Class Registration", ex);
        }

        throw new RuntimeException("An unknown issue occurred during injection.");
    }

    @EventHandler
    public void movePlayer(PlayerJoinEvent event)
    {
        event.setJoinMessage(null);
        event.getPlayer().teleport(spawnLocation);
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void removeQuitMessages(PlayerQuitEvent event)
    {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void disallowBreaking(BlockBreakEvent event)
    {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void disallowPlacing(BlockPlaceEvent event)
    {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void disallowHunger(FoodLevelChangeEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void disallowDamage(EntityDamageEvent event)
    {
        if (event.getEntityType() == EntityType.PLAYER)
        {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID)
                event.getEntity().teleport(spawnLocation);

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void disallowWeatherUpdate(WeatherChangeEvent event)
    {
        event.setCancelled(event.toWeatherState());
    }

    // temp
    @EventHandler
    public void roleWhitelist(PlayerLoginEvent event)
    {
        if (database.cacheFetch(event.getPlayer().getUniqueId()).role.compareTo(Role.BUILDER) >= 0)
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ChatColor.YELLOW + "You are not permitted to join the network yet.");
    }

}
