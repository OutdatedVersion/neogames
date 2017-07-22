package net.neogamesmc.common.payload;

import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;

/**
 * Represents the action of changing the
 * server that a player is connected to.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (1:51 AM)
 */
@Focus ( "req-server-change-raw" )
public class RawSwitchServerPayload implements Payload
{

    /**
     * A group of every player that will be sent
     * via this payload.
     * <p>
     * Every element in this may be either a
     * {@link java.util.UUID} or their username.
     */
    public String[] targets;

    /**
     * The server to send the players to.
     */
    public String server;

    /**
     * Whether or not we're sending multiple
     * players via one request.
     */
    public boolean bulk;

    /**
     * Class Constructor
     *
     * @param server The name
     * @param targets The players to send
     */
    public RawSwitchServerPayload(String server, String... targets)
    {
        this.server = server;
        this.targets = targets;
        this.bulk = targets.length > 1;
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
