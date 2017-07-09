package net.neogamesmc.common.payload;

import lombok.AllArgsConstructor;
import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/08/2017 (3:45 PM)
 */
@AllArgsConstructor
public class OpenNetworkPayload implements Payload
{

    public final boolean open;

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder().add("open", open).done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
