package net.neogamesmc.bungee.distribution;

import com.google.common.collect.Maps;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.neogamesmc.bungee.dynamic.ServerCreator;
import net.neogamesmc.bungee.dynamic.ServerData;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/06/2017 (10:21 PM)
 */
public enum DistributionMethod implements PlayerDistribution
{

    /**
     * RR
     */
    ROUND_ROBBIN
    {
        private Map<String, AtomicInteger> counters = Maps.newHashMap();

        @Override
        public ServerInfo apply(String group, ServerCreator creator)
        {
            val counter = counters.computeIfAbsent(group, ignored -> new AtomicInteger(1));
            int id = creator.serverCountInGroup(group) < (counter.get() + 1) ? counter.getAndSet(1) : counter.getAndIncrement();

            return ProxyServer.getInstance().getServerInfo(group + id);
        }
    },

    /**
     * FTC
     */
    LOWEST_FILL_TO_CAPACITY
    {
        @Override
        public ServerInfo apply(String group, ServerCreator creator)
        {
            val servers = creator.serversInGroup(group);
            ServerInfo best = null;

            for (ServerData current : servers)
            {
                val currentData = ProxyServer.getInstance().getServerInfo(current.name);

                // First time
                if (best == null)
                {
                    best = currentData;
                    continue;
                }

                // Check if it's already full
                if (currentData.getPlayers().size() >= current.maxPlayers)
                    continue;

                // Better than our current?
                if (currentData.getPlayers().size() > best.getPlayers().size())
                    best = currentData;
            }

            return best;
        }
    };

    /**
     * Grab an instance of the provided distributor.
     *
     * @return
     */
    public PlayerDistribution get()
    {
        return this;
    }

}
