package net.neogamesmc.common.payload;

import lombok.AllArgsConstructor;
import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (1:51 AM)
 */
@Focus ( "notify-network-of-server" )
@AllArgsConstructor
public class NotifyNetworkOfServerPayload implements Payload
{

    /**
     * The server name.
     */
    public String name;

    /**
     * The group this server is in.
     */
    public String group;

    /**
     * If {@code true} then we'll add the desired server to the proxy. If not,
     * we'll assume that we will be removing one following the property of: {@link #name}
     */
    public boolean add;

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder().add("name", name).add("group", group).add("add", add).done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
