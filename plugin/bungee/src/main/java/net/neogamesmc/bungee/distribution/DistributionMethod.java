package net.neogamesmc.bungee.distribution;

import com.google.common.collect.Maps;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.neogamesmc.bungee.connection.DataHandler;
import net.neogamesmc.network.api.ConnectedServer;

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
        public ServerInfo apply(String group, DataHandler data)
        {
            val counter = counters.computeIfAbsent(group, ignored -> new AtomicInteger());
            long id = data.groupServerCount(group) > counter.get() + 1 ? counter.getAndSet(1) : counter.getAndIncrement();

            return ProxyServer.getInstance().getServerInfo(group + id);
        }
    },

    /**
     * FTC
     */
    FILL_TO_CAPACITY
    {
        @Override
        public ServerInfo apply(String group, DataHandler data)
        {
            val servers = data.serversInGroup(group);
            ConnectedServer best = null;

            for (ConnectedServer server : servers)
            {
                if (best == null)
                    best = server;

                if (server.onlinePlayers > best.onlinePlayers)
                {
                    best = server;
                }
            }

            return ProxyServer.getInstance().getServerInfo(best.name);
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
