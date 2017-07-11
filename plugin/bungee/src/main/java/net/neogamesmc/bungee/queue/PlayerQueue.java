package net.neogamesmc.bungee.queue;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neogamesmc.bungee.NeoGames;
import net.neogamesmc.bungee.distribution.DistributionMethod;
import net.neogamesmc.bungee.distribution.PlayerDirector;
import net.neogamesmc.bungee.dynamic.ServerCreator;
import net.neogamesmc.common.text.Text;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/08/2017 (1:34 PM)
 */
@Singleton
public class PlayerQueue implements Runnable, Listener
{

    /**
     * Local copy of our creator.
     */
    @Inject private ServerCreator creator;

    /**
     * Local copy our of director.
     */
    @Inject private PlayerDirector director;

    /**
     * Local copy of our plugin.
     */
    @Inject private NeoGames plugin;

    /**
     * A relation of group names to their connection queue.
     */
    private Map<String, Queue<Reservation>> groupQueues = Maps.newConcurrentMap();

    /**
     * A set of every player currently in queue to be connected.
     */
    private Map<String, String> inQueue = Maps.newConcurrentMap();

    /**
     * A set of groups that already have servers in queue to be made.
     */
    private Set<String> alreadyCreating = Sets.newConcurrentHashSet();

    public void queue(String group, String... targets)
    {
        val queue = groupQueues.computeIfAbsent(group, ignored -> new LinkedBlockingQueue<>());

        // Make sure our player isn't already in queue
        boolean allowAdd = true;

        // TODO(Ben): will not work with a group request; such as parties
        for (String target : targets)
        {
            val current = inQueue.get(target);

            if (current != null)
            {
                // Already in queue, but different group requested
                if (!current.equals(group))
                {
                    removeReservation(group, target);
                }
                // Attempted to queue for a group they're already in
                else allowAdd = false;
            }

            // Already disallowed, stop processing
            if (!allowAdd) break;
        }

        if (allowAdd)
        {
            queue.offer(new Reservation(targets.length, targets));
            System.out.println("[Queue] Added request for " + Arrays.toString(targets));
            Arrays.asList(targets).forEach(target -> inQueue.put(target, group));
        }
    }

    public void removeFromQueue(String... targets)
    {
        for (String target : targets)
        {
            val group = inQueue.remove(target);

            if (group != null)
                groupQueues.get(group).removeIf(next -> Arrays.asList(next.targets).contains(target));
        }
    }

    @Override
    public void run()
    {
        long startedAt = System.currentTimeMillis();

        if (!groupQueues.isEmpty())
        {
            groupQueues.forEach((group, queue) ->
            {
                if (queue != null && !queue.isEmpty())
                {
                    val up = queue.peek();
                    val servers = creator.serversInGroup(group);
                    val max = ServerCreator.maxPlayersFromGroup(group);

                    if (servers.isEmpty())
                    {
                        create(group);
                    }
                    else
                    {
                        val info = director.info(group, DistributionMethod.LOWEST_FILL_TO_CAPACITY);

                        if (info != null)
                        {
                            if ((info.getPlayers().size() + up.count) <= max)
                            {
                                System.out.println("[Queue] Connecting " + up.toString() + " to " + info.getName());

                                Arrays.stream(up.targets)
                                      .map(UUID::fromString)
                                      .map(ProxyServer.getInstance()::getPlayer)
                                      .filter(Objects::nonNull)
                                      .forEach(player -> director.connect(player, info));

                                queue.remove();

                                for (String target : up.targets)
                                    inQueue.remove(target);
                            }
                            else
                            {
                                create(group);
                            }
                        }
                        else
                        {
                            create(group);
                        }
                    }
                }
            });
        }

        // Debug
        long time = System.currentTimeMillis() - startedAt;

        if (time >= 4)
            System.out.println("[Queue] High processing time: " + time + "ms :: Group count " + groupQueues.size());
    }

    @EventHandler
    public void fromOnDisconnect(PlayerDisconnectEvent event)
    {
        removeReservation(null, event.getPlayer().getUniqueId().toString());
    }

    /**
     * If a player has been connected to a server they're queued for and
     * they're yet to be removed from the queue system, do it manually.
     *
     * @param event The event
     */
    @EventHandler
    public void cleanup(ServerSwitchEvent event)
    {
        val target = event.getPlayer().getUniqueId().toString();
        val groupTo = Text.stripNumbers(event.getPlayer().getServer().getInfo().getName());

        val inQueueFor = inQueue.get(target);

        if (inQueueFor != null && inQueueFor.equals(groupTo))
        {
            removeReservation(inQueueFor, target);
        }
    }

    /**
     * Remove a reservation for the provided player.
     * <p>
     * If a group is provided we'll only remove reservations
     * for the target in that group -- If not any request will be removed.
     *
     * @param group In the group
     * @param target The target
     */
    private void removeReservation(String group, String target)
    {
        // O(n) op -- Need to cut down on this
        if (group != null)
        {
            if (inQueue.remove(target, group))
                groupQueues.get(group).removeIf(reservation -> Arrays.asList(reservation.targets).contains(target));
        }
        else
        {
            val from = inQueue.remove(target);

            if (from != null)
                groupQueues.get(from).removeIf(reservation -> Arrays.asList(reservation.targets).contains(target));
        }
    }

    /**
     * Create a server in the provided group.
     *
     * @param group the group
     */
    private void create(String group)
    {
        if (!alreadyCreating.contains(group))
        {
            creator.createAndStartServer(group).addListener(() -> alreadyCreating.remove(group), plugin::sync);

            alreadyCreating.add(group);
            System.out.println("[Queue] Requested server creation in group " + group);
        }
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @ToString
    static class Reservation
    {
        int count;
        String[] targets;
    }

}
