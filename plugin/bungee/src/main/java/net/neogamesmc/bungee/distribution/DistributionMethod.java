package net.neogamesmc.bungee.distribution;

import com.google.common.collect.Maps;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.neogamesmc.bungee.dynamic.ServerCreator;
import net.neogamesmc.bungee.dynamic.ServerData;

import java.util.List;
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
            List<ServerData> servers = creator.serversInGroup(group);

            servers.sort((one, two) ->
            {
                int n = Integer.parseInt(one.name.substring(one.name.length() - 1));
                int n2 = Integer.parseInt(two.name.substring(two.name.length() - 1));

                return Integer.compare(n, n2);
            });

            ServerInfo best = null;

            // servers = blastoff1, blastoff2
            for (ServerData current : servers)
            {
                val currentData = ProxyServer.getInstance().getServerInfo(current.name);

                // Check if it's already full
                if (currentData.getPlayers().size() >= current.maxPlayers)
                    continue;

                // Better than our current?
                if (currentData.getPlayers().size() < (best == null ? Integer.MAX_VALUE : best.getPlayers().size()))
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
