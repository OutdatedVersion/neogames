package net.neogamesmc.common.payload;

import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (4:28 AM)
 */
@Focus ( "queue-remove" )
public class RemoveFromQueuePayload implements Payload
{

    /**
     * The targets to remove with this payload.
     */
    public final String[] targets;

    /**
     * Class Constructor
     *
     * @param targets Targets
     */
    public RemoveFromQueuePayload(String... targets)
    {
        this.targets = targets;
    }

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder().add("targets", targets).done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
