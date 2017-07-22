package net.neogamesmc.common.payload;

import lombok.RequiredArgsConstructor;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (2:01 AM)
 */
@Focus ( "request-proxy-action" )
@RequiredArgsConstructor
public class RequestProxyActionPayload implements Payload
{

    /**
     * The action to perform.
     */
    public final Action action;

    /**
     * A representation of what we're doing.
     */
    public enum Action
    {
        PURGE_PUNISHMENT_CACHE
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
