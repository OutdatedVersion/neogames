package net.neogamesmc.network.communication;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import net.neogamesmc.common.payload.RequestServerCreationPayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.network.Coeus;
import net.neogamesmc.network.deploy.DeployType;
import org.pmw.tinylog.Logger;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (7:03 AM)
 */
@Singleton
public class RequestHandler
{

    /**
     * Network manager core instance.
     */
    @Inject private Coeus coeus;

    @Inject
    public RequestHandler(RedisHandler redis)
    {
        redis.registerHook(this);
    }

    /**
     * Handle requests to deploy data.
     *
     * @param payload The payload
     */
    @FromChannel ( RedisChannel.NETWORK )
    @HandlesType ( RequestServerCreationPayload.class )
    @SneakyThrows
    public void handle(RequestServerCreationPayload payload)
    {
        Logger.info("[Requests] Incoming request to provision server for group " + payload.group);
        coeus.deploy(DeployType.ADD_SERVER, payload.group);
    }

}
