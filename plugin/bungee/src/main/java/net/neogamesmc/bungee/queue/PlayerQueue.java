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
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neogamesmc.bungee.distribution.DistributionMethod;
import net.neogamesmc.bungee.distribution.PlayerDirector;
import net.neogamesmc.bungee.dynamic.ServerCreator;
import net.neogamesmc.bungee.event.AddServerEvent;

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
     * A relation of group names to their connection queue.
     */
    private Map<String, Queue<Reservation>> groupQueues = Maps.newConcurrentMap();

    /**
     * A set of every player currently in queue to be connected.
     */
    private Map<String, String> inQueue = Maps.newHashMap();

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
                // Already in queue, but different request
                if (!current.equals(group))
                {
                    // O(n) -- Need to cut down
                    groupQueues.get(current).removeIf(reservation -> Arrays.asList(reservation.targets).contains(target));
                }
                // Attempted to queue for a group they're already in
                else allowAdd = false;
            }
        }

        if (allowAdd)
        {
            queue.offer(new Reservation(targets.length, targets));
            System.out.println("[Queue] Added request for " + Arrays.toString(targets));
            Arrays.asList(targets).forEach(target -> inQueue.put(target, group));
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
                            if ((info.getPlayers().size() + up.count) < max)
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

        System.out.println("[Queue] Elapsed processing time: " + (System.currentTimeMillis() - startedAt) + "ms");
    }

    @EventHandler
    public void removeFromCreating(AddServerEvent event)
    {
        alreadyCreating.remove(event.data.group);
    }

    @EventHandler
    public void removeFromQueue(PlayerDisconnectEvent event)
    {
        inQueue.remove(event.getPlayer().toString());
    }

    private void create(String group)
    {
        if (!alreadyCreating.contains(group))
        {
            creator.createAndStartServer(group);
            alreadyCreating.add(group);
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
