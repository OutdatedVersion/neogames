package net.neogamesmc.core.npc;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.scoreboard.PlayerSidebarManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by nokoa on 5/7/2017.
 * Manages NPC spawning and despawning
 * depedning on player's distance from each NPC
 * <p>
 * Stores, registers, creates, and manipulate NPCs
 */
@Singleton
public class NPCManager implements Listener
{

    /**
     * The distance (in blocks) a player must be to receive the entities.
     */
    private static final int RESPAWN_DISTANCE = 42;

    /**
     * How often to refresh entities.
     */
    private static final long REFRESH_CHECK_INTERVAL = 10;

    /**
     * Managing plugin instance.
     */
    @Inject private Plugin plugin;

    /**
     * Mapping of an entity's ID to the representation.
     */
    private Map<Integer, NPC> tracking = Maps.newHashMap();
    // TODO(Ben): replace with primitive map

    @Inject RedisHandler redis;

    public NPCManager()
    {
        handleSpawns();
    }

    /**
     * Link a {@link PlayerSidebarManager} to this {@code NPCManager}.
     *
     * @param manager Scoreboard manager
     * @return This manager, for chaining
     */
    public NPCManager scoreboard(PlayerSidebarManager manager)
    {
        manager.addDefaultModifier(plugin.get(NPCModifier.class));
        return this;
    }

    public NPC create(NPCType type, String name, Location location)
    {
        NPC npc = new NPC(type, name, location);

        tracking.put(npc.getEntityID(), npc);

        return npc;
    }

    /**
     * Spawn all NPCs on player join
     *
     * @param event
     */
    @EventHandler
    public void onPlayerjOin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        PacketReader packetReader = new PacketReader(player, this);
        packetReader.inject();

        Iterator npcsIteator = tracking.entrySet().iterator();
        while (npcsIteator.hasNext())
        {
            Map.Entry pair = (Map.Entry) npcsIteator.next();
            NPC npc = (NPC) pair.getValue();

            npc.spawn(player);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
            {
                npc.removeNPCInfo(player);
            }, 40l);
        }

    }

    /**
     * Checks distance from player to NPC and determines whether
     * the npc needs to be respawned or destroyed
     */
    public void handleSpawns()
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
        {
            Iterator npcsIteator = tracking.entrySet().iterator();
            while (npcsIteator.hasNext())
            {
                Map.Entry pair = (Map.Entry) npcsIteator.next();
                NPC npc = (NPC) pair.getValue();
                for (Player player : npc.getViewers())
                {
                    if (npc.getLocation().distance(player.getLocation()) <= RESPAWN_DISTANCE && !npc.canPlayerSee(player))
                    {
                        npc.spawn(player);
                        npc.addPlayerToVisibleList(player);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        {
                            npc.removeNPCInfo(player);
                        }, 40l);

                    }
                    else if (npc.getLocation().distance(player.getLocation()) > RESPAWN_DISTANCE && npc.canPlayerSee(player))
                    {
                        npc.removePlayerFromVisibleList(player);
                        npc.destroy(player);
                    }
                }


            }
        }, REFRESH_CHECK_INTERVAL, REFRESH_CHECK_INTERVAL);
    }

    public HashMap<Integer, NPC> getTracking()
    {
        return tracking;
    }
}
