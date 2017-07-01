package net.neogamesmc.common.backend;

import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (7:05 AM)
 */
@Focus ( "req-create-server" )
public class RequestServerCreationPayload implements Payload
{

    public String player;
    public String type;
    public String data;

    public RequestServerCreationPayload(String player, String type, String data)
    {
        this.player = player;
        this.type = type;
        this.data = data;
    }

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder()
                .add("player", player)
                .add("type", type)
                .add("data", data)
                .done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
