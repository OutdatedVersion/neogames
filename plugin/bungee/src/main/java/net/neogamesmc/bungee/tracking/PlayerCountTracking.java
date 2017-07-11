package net.neogamesmc.bungee.tracking;

import com.google.inject.Inject;
import net.md_5.bungee.api.ProxyServer;
import net.neogamesmc.common.redis.RedisHandler;
import redis.clients.jedis.Jedis;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/11/2017 (5:11 AM)
 */
public class PlayerCountTracking implements Runnable
{

    /**
     * Local copy of our Redis instance.
     */
    @Inject private RedisHandler redis;

    @Override
    public void run()
    {
        try (Jedis jedis = redis.client())
        {
            jedis.set("network:player_count", String.valueOf(ProxyServer.getInstance().getPlayers().size()));
        }
    }

}
