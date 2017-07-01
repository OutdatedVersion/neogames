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
import net.neogamesmc.core.hotbar.HotbarItem;
import net.neogamesmc.core.inventory.ItemBuilder;
import net.neogamesmc.core.issue.Issues;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftSkeleton;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import static org.bukkit.Material.COMPASS;

/**
 * Startup function(s) for a main lobby.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/19/2017 (3:30 AM)
 */
public class Lobby extends Plugin implements Listener {

    // small magma cube : blast off - red green bold Join
    // green villager : chunk runner - green
    // skeleton w/ bow : bow plinko - purple

    /**
     * Where players are sent when joining.
     */
    private Location spawnLocation;

    /**
     * Local instance
     */
    private Database database;

    @Override
    public void enable(Injector injector) {
        // Forcefully start database first
        this.database = register(Database.class);

        register(RedisHandler.class).init().subscribe(RedisChannel.DEFAULT);
        register(CommandHandler.class).addProviders(CommandHandler.DEFAULT_PROVIDERS).registerInPackage("net.neogamesmc.core");

        System.out.println("Beginning class-path scan..");

        new FastClasspathScanner("net.neogamesmc").addClassLoader(getClassLoader()).matchClassesWithAnnotation(ParallelStartup.class, this::register).scan();

        getServer().getPluginManager().registerEvents(this, this);


        this.spawnLocation = new Location(Bukkit.getWorld("lobby"), 10.5, 64.5, 5.5, 177.4f, -12.4f);
        this.spawnLocation.getChunk().load();

        MagmaCube magmaCube = spawnLocation.getWorld().spawn(new Location(spawnLocation.getWorld(), 3.5, 63, 1.5, -128.5f, 12.5f), MagmaCube.class);
        magmaCube.setSize(2);
        magmaCube.setAI(false);
        magmaCube.setInvulnerable(true);
        magmaCube.setSilent(true);
        magmaCube.setCollidable(false);
        magmaCube.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Join " + ChatColor.RED + "" + ChatColor.BOLD + "Blast Off" + ChatColor.RESET);
        magmaCube.setCustomNameVisible(true);
        magmaCube.setMetadata("send-server", new FixedMetadataValue(this, "BLAST_OFF"));
        ((CraftSkeleton) magmaCube).getHandle().setPositionRotation(3.5, 63, 1.5, -128.5f, 0);
        ((CraftSkeleton) magmaCube).getHandle().h(-128.5f);

        Skeleton skeleton = spawnLocation.getWorld().spawn(new Location(spawnLocation.getWorld(), -0.5, 63.06, -2.5, -48.3f, 0f), Skeleton.class);
        skeleton.setAI(false);
        skeleton.setInvulnerable(true);
        skeleton.setSilent(true);
        skeleton.setCollidable(false);
        skeleton.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Join " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Bowplinko" + ChatColor.RESET);
        skeleton.setCustomNameVisible(true);
        skeleton.setMetadata("send-server", new FixedMetadataValue(this, "BOWPLINKO"));
        skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.BOW, 1));
        ((CraftSkeleton) skeleton).getHandle().setPositionRotation(-0.5, 63.06, -2.5, -48.3f, 0f);
        ((CraftSkeleton) skeleton).getHandle().h(-48.3f);

        Villager villager = spawnLocation.getWorld().spawn(new Location(spawnLocation.getWorld(), -1.147, 63.06250, 2.5, -80, 0), Villager.class);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCollidable(false);
        villager.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Join " + ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Chunk Runner" + ChatColor.RESET);
        villager.setCustomNameVisible(true);
        villager.setMetadata("send-server", new FixedMetadataValue(this, "CHUNK_RUNNER"));
        ((CraftSkeleton) skeleton).getHandle().setPositionRotation(-1.147, 63.06250, 2.5, -80, 0);
        ((CraftSkeleton) skeleton).getHandle().h(-80f);
    }

    @Override
    public void disable() {
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
    public <T> T register(final Class<T> clazz) {
        try {
            T obj = get(clazz);

            // auto-register event listeners
            if (obj instanceof Listener)
                getServer().getPluginManager().registerEvents((Listener) obj, this);

            return obj;
        } catch (Exception ex) {
            Issues.handle("Class Registration", ex);
        }

        throw new RuntimeException("An unknown issue occurred during injection.");
    }

    @EventHandler
    public void movePlayer(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        event.getPlayer().teleport(spawnLocation);

        new HotbarItem(event.getPlayer(), new ItemBuilder(COMPASS).name("Select a game").build())
                .location(1)
                .action(Action.RIGHT_CLICK_AIR, player ->
                {

                });
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!event.getEntity().hasMetadata("send-server"))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void removeQuitMessages(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void disallowBreaking(BlockBreakEvent event) {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void disallowPlacing(BlockPlaceEvent event) {
        event.setCancelled(event.getPlayer().getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void disallowHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void disallowDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID)
                event.getEntity().teleport(spawnLocation);

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void disallowWeatherUpdate(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }

    // temp
    @EventHandler
    public void roleWhitelist(PlayerLoginEvent event) {
        if (database.cacheFetch(event.getPlayer().getUniqueId()).role.compareTo(Role.BUILDER) >= 0)
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ChatColor.YELLOW + "You are not permitted to join the network yet.");
    }

    @EventHandler
    public void npcHandler(PlayerInteractEntityEvent event) {
        if (!event.getRightClicked().hasMetadata("send-server"))
            return;

        event.setCancelled(true);
        String server = event.getRightClicked().getMetadata("send-server").get(0).asString();
    }

    @EventHandler
    public void stopSmokingWeed(EntityCombustEvent event) {
        event.setCancelled(event.getEntity().hasMetadata("send-server"));
    }


    public void openNavigationMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Join Game");

        ItemBuilder chunkRunnerItemBuilder = new ItemBuilder(Material.GRASS);
        chunkRunnerItemBuilder.name(ChatColor.GREEN + "Chunck Runner");
        chunkRunnerItemBuilder.lore(
                ChatColor.DARK_GRAY + "Parkour/Challenge",
                "",
                ChatColor.GRAY + "Run along a parkour course that",
                ChatColor.GRAY + "generates in one direction and",
                ChatColor.GRAY + "crumbles away behind you at an",
                ChatColor.GRAY + "increasing speed!",
                "",
                ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMC",
                ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky & FantomLX",
                ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "1 - 24 Players");


        inventory.setItem(11, chunkRunnerItemBuilder.build());
        inventory.setItem(2, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13));
        inventory.setItem(20, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13));

        ItemBuilder blastOffItemBuilder = new ItemBuilder(Material.FIREWORK_CHARGE);
        blastOffItemBuilder.name(ChatColor.RED + "Blast Off");
        blastOffItemBuilder.lore(
                ChatColor.DARK_GRAY + "Minigame/Pvp",
                "",
                ChatColor.GRAY + "Use your arsenal of exploding weapons",
                ChatColor.GRAY + "and tons of powerups to blast apart",
                ChatColor.GRAY + "the map! Be the last player standing",
                ChatColor.GRAY + "to win!",
                "",
                ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMc",
                ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky, Falcinspire, Dennisbuilds,",
                ChatColor.BLUE + "ItsZender, Jayjo, Corey977, JacobRuby, Team Dracolyte & StainMine",
                ChatColor.GRAY + "Support: " + ChatColor.YELLOW + "2 - 12 Players");

        inventory.setItem(13, blastOffItemBuilder.build());
        inventory.setItem(4, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));
        inventory.setItem(22, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        ItemBuilder bowplinkoItemBuilder = new ItemBuilder(Material.BOW);
        bowplinkoItemBuilder.name(ChatColor.DARK_PURPLE + "Bowplinko");
        bowplinkoItemBuilder.lore(
                ChatColor.DARK_GRAY + "Minigame/Archery",
                "",
                ChatColor.GRAY + "A fast-paced archery war between",
                ChatColor.GRAY + "two teams, but with a twist.",
                ChatColor.GRAY + "If you get hit, you fall down",
                ChatColor.GRAY + "a plinko board!",
                "",
                ChatColor.GRAY + "Developer: " + ChatColor.GOLD + "NeoMc",
                ChatColor.GRAY + "Credit: " + ChatColor.BLUE + "iWacky",
                ChatColor.GRAY + "Supports: " + ChatColor.YELLOW + "2 - 24 Players");

        inventory.setItem(15, bowplinkoItemBuilder.build());
        inventory.setItem(6, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));
        inventory.setItem(24, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));

        player.openInventory(inventory);

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        if (inventory.getName() == null)
            return;

        if (inventory.getName().equalsIgnoreCase(ChatColor.GREEN + "Join Game")) {
            event.setCancelled(true);

            int slot = event.getSlot();
            switch (slot){
                case 11:
                    player.sendMessage("You pressed on Chuck Runner");
                    break;
                case 13:
                    player.sendMessage("You pressed on Blast Off");
                    break;
                case 15:
                    player.sendMessage("You pressed on Bowplink");
                    break;
            }
        }
    }
}
