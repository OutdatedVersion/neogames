package net.neogamesmc.common.payload;

import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/08/2017 (2:22 PM)
 */
@Focus ( "queue-player" )
public class QueuePlayersForGroupPayload implements Payload
{

    /**
     * The group we want to send the {@link #targets} to.
     */
    public final String group;

    /**
     * The players that we'd like to send.
     */
    public final String[] targets;

    /**
     * Class Constructor
     *
     * @param group The group
     * @param targets The targets
     */
    public QueuePlayersForGroupPayload(String group, String... targets)
    {
        this.group = group;
        this.targets = targets;
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
