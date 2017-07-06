package net.neogamesmc.lobby;

import com.destroystokyo.paper.Title;
import com.google.inject.Binder;
import com.google.inject.Injector;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import lombok.val;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.payload.SwitchServerPayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.command.api.CommandHandler;
import net.neogamesmc.core.hotbar.HotbarHandler;
import net.neogamesmc.core.hotbar.HotbarItem;
import net.neogamesmc.core.inventory.ItemBuilder;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.scheduler.Scheduler;
import net.neogamesmc.core.scoreboard.PlayerSidebarManager;
import net.neogamesmc.core.text.Colors;
import net.neogamesmc.lobby.news.News;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftMagmaCube;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftVillager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.ThreadLocalRandom;

import static net.md_5.bungee.api.ChatColor.*;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;
import static net.neogamesmc.core.text.Colors.bold;
import static org.bukkit.Material.COMPASS;

/**
 * Startup function(s) for a main lobby.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/19/2017 (3:30 AM)
 */
public class Lobby extends Plugin implements Listener
{

    /**
     * The beginning of the role.
     */
    private static final Title.Builder BUILDER = new Title.Builder()
            .title(new ComponentBuilder("Neo").bold(true).color(YELLOW).append("Games").color(GOLD).create())
            .stay(20 * 3);

    /**
     * Where players are sent when joining.
     */
    private Location spawnLocation;

    /**
     * Local instance
     */
    private Database database;

    /**
     * Maintain our lobby scoreboard.
     */
    private LobbyScoreboard scoreboard;


    @Override
    public void setupInjector(Binder binder)
    {
        binder.requestStaticInjection(Scheduler.class);
    }

    @Override
    public void enable(Injector injector)
    {
        // Forcefully start database first
        this.database = register(Database.class);

        get(RedisHandler.class).subscribe(RedisChannel.DEFAULT);
        register(News.class);
        register(HotbarHandler.class);
        register(CommandHandler.class)
                .addProviders(CommandHandler.DEFAULT_PROVIDERS)
                .registerInPackage("net.neogamesmc.core");

        register(PlayerSidebarManager.class);
        scoreboard = register(LobbyScoreboard.class);

        System.out.println("Beginning class-path scan..");

        new FastClasspathScanner("net.neogamesmc")
                .addClassLoader(getClassLoader())
                .matchClassesWithAnnotation(ParallelStartup.class, this::register)
                .scan();

        getServer().getPluginManager().registerEvents(this, this);


        val lobby = Bukkit.getWorld("lobby");
        this.spawnLocation = new Location(lobby, 10.5, 64, 5.5, 162.6f, 0f);
        lobby.loadChunk(-1, 0);
        lobby.loadChunk(-1, -1);

        MagmaCube magmaCube = lobby.spawn(new Location(lobby, 3.5, 63, 1.5, -128.5f, 12.5f), MagmaCube.class);
        magmaCube.setSize(2);
        magmaCube.setAI(false);
        magmaCube.setInvulnerable(true);
        magmaCube.setSilent(true);
        magmaCube.setCollidable(false);
        magmaCube.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Join " + ChatColor.RED + "" + ChatColor.BOLD + "Blast Off" + ChatColor.RESET);
        magmaCube.setCustomNameVisible(true);
        magmaCube.setMetadata("send-server", new FixedMetadataValue(this, "blastoff"));
        ((CraftMagmaCube) magmaCube).getHandle().setPositionRotation(3.5, 63, 1.5, -60.8f, 0);
        ((CraftMagmaCube) magmaCube).getHandle().h(-60.8f);

        Skeleton skeleton = lobby.spawn(new Location(lobby, -0.5, 63.06, -2.5, -48.3f, 0f), Skeleton.class);
        skeleton.setAI(false);
        skeleton.setInvulnerable(true);
        skeleton.setSilent(true);
        skeleton.setCollidable(false);
        skeleton.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Join " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Bowplinko" + ChatColor.RESET);
        skeleton.setCustomNameVisible(true);
        skeleton.setMetadata("send-server", new FixedMetadataValue(this, "bowplinko"));
        skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.BOW, 1));
        ((CraftSkeleton) skeleton).getHandle().setPositionRotation(-0.5, 63.06, -2.5, -48.3f, 0f);
        ((CraftSkeleton) skeleton).getHandle().h(-48.3f);

        Villager villager = lobby.spawn(new Location(lobby, -1.147, 63.06250, 2.5, -80, 0), Villager.class);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCollidable(false);
        villager.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Join " + ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Chunk Runner" + ChatColor.RESET);
        villager.setCustomNameVisible(true);
        villager.setMetadata("send-server", new FixedMetadataValue(this, "chunkrunner"));
        ((CraftVillager) villager).getHandle().setPositionRotation(-1.147, 63.06250, 2.5, -80, 0);
        ((CraftVillager) villager).getHandle().h(-80f);
    }

    @Override
    public void disable()
    {
        get(Database.class).release();

        spawnLocation.getWorld().getEntities().stream().filter(entity -> entity.hasMetadata("send-server")).forEach(Entity::remove);
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

    @EventHandler ( priority = EventPriority.LOWEST )
    public void movePlayer(PlayerJoinEvent event)
    {
        event.setJoinMessage(null);
        event.getPlayer().teleport(spawnLocation);

        new HotbarItem(event.getPlayer(), new ItemBuilder(COMPASS).name(bold(DARK_GREEN) + "Game Selection").build())
                .location(0)
                .action(Action.RIGHT_CLICK_AIR, this::openNavigationMenu)
                .add(get(HotbarHandler.class));

        scoreboard.create(event.getPlayer());

        event.getPlayer().sendTitle(BUILDER.subtitle(new ComponentBuilder("Welcome back, ")
                .append(event.getPlayer().getName()).color(DARK_GREEN).append("!", NONE).create()).build());
    }

    @EventHandler
    public void cleanup(PlayerQuitEvent event)
    {
        scoreboard.destroy(event.getPlayer());
    }

    @EventHandler
    public void disallowDrop(PlayerDropItemEvent event)
    {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void disallowMovement(InventoryClickEvent event)
    {
        event.setCancelled(event.getWhoClicked().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void disallowMovement(PlayerSwapHandItemsEvent event)
    {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        event.setCancelled(event.getEntity().hasMetadata("send-server"));
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
    public void disallowDamage(EntityDamageByEntityEvent event)
    {
        if (event.getEntityType() == EntityType.PLAYER)
            event.setCancelled(true);
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
        if (database.cacheFetch(event.getPlayer().getUniqueId()).role().compareTo(Role.YOUTUBE) >= 0)
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ChatColor.YELLOW + "You are not permitted to join the network yet.");
    }

    @EventHandler
    public void npcHandler(PlayerInteractEntityEvent event)
    {
        if (!event.getRightClicked().hasMetadata("send-server"))
            return;

        event.setCancelled(true);
        new SwitchServerPayload(event.getRightClicked().getMetadata("send-server").get(0).asString() + ThreadLocalRandom.current().nextInt(1, 2), event.getPlayer().getUniqueId().toString()).publish(get(RedisHandler.class));
    }

    @EventHandler
    public void keepEntityFromCatchingOnFire(EntityCombustEvent event)
    {
        event.setCancelled(true);
    }


    public void openNavigationMenu(Player player)
    {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Join Game");

        ItemBuilder chunkRunnerItemBuilder = new ItemBuilder(Material.GRASS);
        chunkRunnerItemBuilder.name(Colors.bold(GREEN) + "Chunk Runner");
        chunkRunnerItemBuilder.lore(ChatColor.DARK_GRAY + "Parkour/Challenge", "", ChatColor.GRAY + "Run along a parkour course that", ChatColor.GRAY + "generates in one direction and", ChatColor.GRAY + "crumbles away behind you at an", ChatColor.GRAY + "increasing speed!", "", ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMc", ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky & FantomLX", ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "1 - 24 Players");


        inventory.setItem(11, chunkRunnerItemBuilder.build());
        inventory.setItem(2, glass(13));
        inventory.setItem(20, glass(13));

        ItemBuilder blastOffItemBuilder = new ItemBuilder(Material.FIREBALL);
        blastOffItemBuilder.name(Colors.bold(net.md_5.bungee.api.ChatColor.RED) + "Blast Off");
        blastOffItemBuilder.lore(ChatColor.DARK_GRAY + "Minigame/PvP", "", ChatColor.GRAY + "Use your arsenal of exploding weapons", ChatColor.GRAY + "and tons of powerups to blast apart", ChatColor.GRAY + "the map! Be the last player standing", ChatColor.GRAY + "to win!", "", ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMc", ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky, Falcinspire, Dennisbuilds,", ChatColor.BLUE + "ItsZender, Jayjo, Corey977, JacobRuby,", ChatColor.BLUE + "Team Dracolyte & StainMine", ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "2 - 12 Players");

        inventory.setItem(13, blastOffItemBuilder.build());
        inventory.setItem(4, glass(1));
        inventory.setItem(22, glass(1));

        ItemBuilder bowplinkoItemBuilder = new ItemBuilder(Material.BOW);
        bowplinkoItemBuilder.name(Colors.bold(net.md_5.bungee.api.ChatColor.DARK_PURPLE) + "Bowplinko");
        bowplinkoItemBuilder.lore(ChatColor.DARK_GRAY + "Minigame/Archery", "", ChatColor.GRAY + "A fast-paced archery war between", ChatColor.GRAY + "two teams, but with a twist.", ChatColor.GRAY + "If you get hit, you fall down", ChatColor.GRAY + "a plinko board!", "", ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMc", ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky", ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "2 - 24 Players");

        inventory.setItem(15, bowplinkoItemBuilder.build());
        inventory.setItem(6, glass(10));
        inventory.setItem(24, glass(10));

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (inventory.getName() == null)
            return;

        if (inventory.getName().equals(ChatColor.GREEN + "Join Game"))
        {
            event.setCancelled(true);

            switch (event.getSlot())
            {
                case 11:
                    player.sendMessage("You pressed on Chuck Runner");
                    break;
                case 13:
                    player.sendMessage("You pressed on Blast Off");
                    break;
                case 15:
                    player.sendMessage("You pressed on Bowplinko");
                    break;
            }
        }
    }

    /**
     * @param data The color
     * @return The glass
     */
    private static ItemStack glass(int data)
    {
        return new ItemBuilder(Material.STAINED_GLASS_PANE).byteData((byte) data).name(" ").build();
    }

}
