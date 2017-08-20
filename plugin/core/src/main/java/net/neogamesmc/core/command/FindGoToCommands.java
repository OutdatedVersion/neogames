package net.neogamesmc.core.command;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import lombok.val;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.common.payload.RawSwitchServerPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.message.Message;
import net.neogamesmc.core.message.option.format.Color;
import net.neogamesmc.core.scheduler.Scheduler;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/10/2017 (5:04 AM)
 */
public class FindGoToCommands
{

    /**
     * Local copy of our Redis instance.
     */
    @Inject private RedisHandler redis;

    /**
     * Local copy of our server data.
     */
    @Inject private ServerConfiguration config;

    /**
     * A cache mapping player's names to their current server.
     * <p>
     * If we do not have a current entry then fetch it from Redis.
     */
    private final LoadingCache<String, String> locationCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build(new CacheLoader<String, String>()
    {
        @Override
        public String load(String key) throws Exception
        {
            try (Jedis jedis = redis.client())
            {
                val result = jedis.hget("network:players:" + key.toLowerCase(), "server");

                return result == null ? "NO" : result;
            }
        }
    });

    /**
     * Expose a method to find a player.
     *
     * @param player The person running the command
     * @param target The person we're looking for
     */
    @Command ( executor = { "find", "locate", "sniff" } )
    @Permission ( Role.MOD )
    public void locateCommand(Player player, @Necessary ( "You must provide a target's name" ) String target)
    {
        Scheduler.async(() ->
        {
            try
            {
                val server = locationCache.get(target);

                if (server.equals("NO"))
                    Message.prefix("Locator").player(target).content("is not currently online").send(player);
                else
                    Message.prefix("Locator").player(target).content("is on").content(server, Color.YELLOW).send(player);
            }
            catch (Exception ex)
            {
                Issues.handle("Fetch Player Location", ex);
            }
        });
    }

    /**
     * Expose a method to go to a player's location.
     *
     * @param player The player
     * @param target The target
     */
    @Command ( executor = "goto" )
    @Permission ( Role.MOD )
    public void gotoCommand(Player player, @Necessary ( "You must provide a target's name" ) String target)
    {
        Scheduler.async(() ->
        {
            try
            {
                val server = locationCache.get(target);

                if (server.equals("NO"))
                {
                    Message.prefix("Go To").player(target).content("is not currently online").send(player);
                }
                else
                {
                    if (server.equalsIgnoreCase(config.name))
                    {
                        Message.prefix("Go To").content("You and").player(target).content("are already on the same server").send(player);
                    }
                    else
                    {
                        new RawSwitchServerPayload(server, player.getUniqueId().toString()).publish(redis);
                        Message.prefix("Go To").content("Sending you to").player(target).content("(" + server + ")").send(player);
                    }
                }
            }
            catch (Exception ex)
            {
                Issues.handle("Fetch Player Location (Go To)", ex);
            }
        });
    }

}
