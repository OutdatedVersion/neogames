package net.neogamesmc.bungee.connection;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.neogamesmc.bungee.distribution.DistributionMethod;
import net.neogamesmc.common.payload.NotifyServerCreationPayload;
import net.neogamesmc.common.payload.UpdateNetworkServersPayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.network.api.ConnectedServer;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Set;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/06/2017 (10:22 PM)
 */
@Singleton
public class DataHandler
{

    private RedisHandler redis;
    private Gson gson = new Gson();

    private Map<String, ConnectedServer> servers = Maps.newConcurrentMap();

    @Inject
    public DataHandler(RedisHandler redis)
    {
        this.redis = redis;
        redis.registerHook(this);
    }

    @FromChannel( RedisChannel.NETWORK)
    @HandlesType( NotifyServerCreationPayload.class)
    public void addToMap(NotifyServerCreationPayload payload)
    {
        val data = gson.fromJson(payload.obj, ConnectedServer.class);

        servers.put(data.name, data);
    }

    @FromChannel( RedisChannel.NETWORK)
    @HandlesType( UpdateNetworkServersPayload.class)
    public void remove(UpdateNetworkServersPayload payload)
    {
        if (!payload.add)
            servers.remove(payload.name);
    }

    public Long groupServerCount(String group)
    {
        // TODO(Ben): i really like how non-blocking this it

        try (Jedis jedis = redis.client())
        {
            return Long.parseLong(jedis.hget("network:groups:" + group, "server_count"));
        }
    }

    public Set<ConnectedServer> serversInGroup(String group)
    {
        long count = groupServerCount(group);
        Set<ConnectedServer> set = Sets.newHashSet();

        for (int i = 1; i < count; i++)
            set.add(servers.get(group + i));

        return set;
    }

    public DistributionMethod methodFor(String group)
    {
        return group.equalsIgnoreCase("lobby") ? DistributionMethod.ROUND_ROBBIN : DistributionMethod.FILL_TO_CAPACITY;
    }

}
