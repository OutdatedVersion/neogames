package net.neogamesmc.bungee.network;

import com.google.inject.Inject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.neogamesmc.common.payload.SwitchServerPayload;
import net.neogamesmc.common.payload.UpdateNetworkServersPayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;

import java.net.InetSocketAddress;

import static java.util.UUID.fromString;

/**
 * Process action requests from Redis.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (1:47 AM)
 */
public class MessageHandler
{

    /**
     * Our BungeeCord proxy instance.
     */
    @Inject private ProxyServer proxy;

    /**
     * Class Constructor
     *
     * @param redis Redis instance
     */
    @Inject
    public MessageHandler(RedisHandler redis)
    {
        redis.registerHook(this);
        System.out.println("[Network] Registering message hook");
    }

    /**
     * Respond to requests to move a player's server.
     *
     * @param payload The payload
     */
    @FromChannel ( RedisChannel.NETWORK )
    @HandlesType ( SwitchServerPayload.class )
    public void switchServers(SwitchServerPayload payload)
    {
        final ServerInfo server = proxy.getServerInfo(payload.server);

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

    /**
     * Respond to requests to add servers.
     *
     * @param payload The payload
     */
    @FromChannel ( RedisChannel.NETWORK )
    @HandlesType ( UpdateNetworkServersPayload.class )
    public void updateNetworkServers(UpdateNetworkServersPayload payload)
    {
        if (payload.add)
        {
            System.out.println("[Network] Adding server by name: " + payload.name);

            proxy.getServers().put(payload.name, proxy.constructServerInfo(
                    payload.name, new InetSocketAddress(payload.port), null, false
            ));
        }
        else
        {
            System.out.println("[Network] Removing server by name: " + payload.name);
            proxy.getServers().remove(payload.name);
        }
    }

}
