package net.neogamesmc.network;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.neogamesmc.common.payload.NotifyServerCreationPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.network.api.ConnectedServer;
import redis.clients.jedis.Jedis;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/06/2017 (7:33 PM)
 */
public class ServerDataBroker
{

    private Gson gson;
    @Inject private RedisHandler redis;

    public void record(ConnectedServer server)
    {
        try (Jedis jedis = redis.client())
        {
            jedis.hincrBy("network:groups:" + server.group, "server_count", 1);
            redis.publish(new NotifyServerCreationPayload(gson.toJson(server)));
        }
    }

    public void remove(ConnectedServer server)
    {
        try (Jedis jedis = redis.client())
        {
            jedis.hincrBy("network:groups:" + server.group, "server_count", -1);
        }
    }

}
