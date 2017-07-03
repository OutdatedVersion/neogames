package net.neogamesmc.common.payload;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class RequestServerCreationPayload implements Payload
{

    /**
     * The name of the player who requested this.
     * <p>
     * {@code null} if the system did so.
     */
    public String player;

    /**
     * The type of server to create.
     */
    public String type;

    /**
     * Any extra data for our manager to use.
     */
    public String data;

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
