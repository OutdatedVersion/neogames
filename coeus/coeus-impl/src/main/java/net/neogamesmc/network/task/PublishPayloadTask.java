package net.neogamesmc.network.task;

import com.google.inject.Inject;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.Payload;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (4:18 AM)
 */
public class PublishPayloadTask implements Task
{

    /**
     * Our Redis instance.
     */
    @Inject private static RedisHandler redis;

    /**
     * The payload to punish.
     */
    private Payload payload;

    /**
     * Class Constructor
     *
     * @param payload The payload
     */
    public PublishPayloadTask(Payload payload)
    {
        this.payload = payload;
    }

    @Override
    public PublishPayloadTask target(String val)
    {
        return this;
    }

    @Override
    public void execute() throws Exception
    {
        payload.publish(redis);
    }

}
