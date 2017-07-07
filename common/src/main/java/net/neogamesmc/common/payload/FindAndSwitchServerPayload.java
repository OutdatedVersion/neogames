package net.neogamesmc.common.payload;

import lombok.AllArgsConstructor;
import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (5:48 AM)
 */
@Focus ( "req-server-change" )
@AllArgsConstructor
public class FindAndSwitchServerPayload implements Payload
{

    /**
     * UUIDs of the players to switch out.
     */
    public String[] targets;

    /**
     * The group the player is requesting.
     */
    public String group;

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder()
                .add("targets", targets)
                .add("group", group)
                .done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
