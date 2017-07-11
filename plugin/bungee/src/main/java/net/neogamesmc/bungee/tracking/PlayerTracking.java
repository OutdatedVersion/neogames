package net.neogamesmc.bungee.tracking;

import com.google.inject.Inject;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neogamesmc.bungee.NeoGames;
import net.neogamesmc.common.redis.RedisHandler;
import redis.clients.jedis.Jedis;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/10/2017 (4:37 AM)
 */
public class PlayerTracking implements Listener
{

    /**
     * Local copy of our Redis handler.
     */
    @Inject private RedisHandler redis;

    /**
     * Locally held copy of our plugin.
     */
    @Inject private NeoGames plugin;

    @EventHandler
    public void record(ServerConnectedEvent event)
    {
        plugin.async(() ->
        {
            try (Jedis jedis = redis.client())
            {
                jedis.hset("network:players:" + event.getPlayer().getName().toLowerCase(), "server", event.getServer().getInfo().getName());
            }
        });
    }

    @EventHandler
    public void remove(PlayerDisconnectEvent event)
    {
        plugin.async(() ->
        {
            try (Jedis jedis = redis.client())
            {
                jedis.hdel("network:players:" + event.getPlayer().getName().toLowerCase(), "server");
            }
        });
    }

}
