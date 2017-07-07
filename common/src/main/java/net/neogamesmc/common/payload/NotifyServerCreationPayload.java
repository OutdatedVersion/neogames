package net.neogamesmc.common.payload;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (5:11 PM)
 */
@Focus("notify-server-create")
@AllArgsConstructor
public class NotifyServerCreationPayload implements Payload
{

    private static final JSONParser parser = new JSONParser();

    public String obj;

    @Override
    @SneakyThrows
    public JSONObject asJSON()
    {
        // todo i want todie

        return (JSONObject) parser.parse(obj);
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }
}
