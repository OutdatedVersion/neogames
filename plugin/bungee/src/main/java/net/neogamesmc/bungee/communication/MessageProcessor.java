package net.neogamesmc.bungee.communication;

import com.google.inject.Inject;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.neogamesmc.bungee.NeoGames;
import net.neogamesmc.bungee.distribution.PlayerDirector;
import net.neogamesmc.bungee.dynamic.ServerCreator;
import net.neogamesmc.bungee.queue.PlayerQueue;
import net.neogamesmc.common.payload.*;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;

import static java.util.UUID.fromString;

/**
 * Process action requests from Redis.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (1:47 AM)
 */
public class MessageProcessor
{

    /**
     * Our BungeeCord proxy instance.
     */
    @Inject private ProxyServer proxy;

    /**
     * Commonly shared plugin instance.
     */
    @Inject private NeoGames plugin;

    /**
     * Send players out to different servers by their request.
     */
    @Inject private PlayerDirector director;

    /**
     * Ability to create servers on demand.
     */
    @Inject private ServerCreator creator;

    /**
     * Expose way to queue players to groups.
     */
    @Inject private PlayerQueue queue;

    /**
     * Class Constructor
     *
     * @param redis Redis instance
     */
    @Inject
    public MessageProcessor(RedisHandler redis)
    {
        redis.registerHook(this);
    }

    /**
     * Respond to requests to move a player's server.
     *
     * @param payload The payload
     */
    @FromChannel ( RedisChannel.NETWORK )
    @HandlesType ( RawSwitchServerPayload.class )
    public void switchServers(RawSwitchServerPayload payload)
    {
        val server = proxy.getServerInfo(payload.server);

        if (server != null)
        {
            for (String target : payload.targets)
            {
                // assumes UUID if it's longer than a username
                if (target.length() > 16)
                    proxy.getPlayer(fromString(target)).connect(server);
                else
                    proxy.getPlayer(target).connect(server);
            }
        }
    }

    @FromChannel ( RedisChannel.NETWORK )
    @HandlesType ( FindAndSwitchServerPayload.class )
    public void switchServers(FindAndSwitchServerPayload payload)
    {
        for (String target : payload.targets)
        {
            director.sendPlayer(ProxyServer.getInstance().getPlayer(fromString(target)), payload.group);
        }
    }

    @FromChannel ( RedisChannel.NETWORK )
    @HandlesType ( RequestServerCreationPayload.class )
    public void createServer(RequestServerCreationPayload payload)
    {
        creator.createAndStartServer(payload.group);
    }

    @FromChannel ( RedisChannel.NETWORK )
    @HandlesType ( QueuePlayersForGroupPayload.class )
    public void queuePlayers(QueuePlayersForGroupPayload payload)
    {
        queue.queue(payload.group, payload.targets);
    }

    /**
     * Respond to requests to add servers.
     *
     * @param payload The payload
     */
    @FromChannel ( RedisChannel.NETWORK )
    @HandlesType ( NotifyNetworkOfServerPayload.class )
    public void updateNetworkServers(NotifyNetworkOfServerPayload payload)
    {
        if (payload.add)
            creator.addServer(payload.name);
        else
            creator.removeServer(payload.group, payload.name);
    }

}
