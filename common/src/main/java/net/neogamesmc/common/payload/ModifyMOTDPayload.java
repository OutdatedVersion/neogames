package net.neogamesmc.common.payload;

import lombok.AllArgsConstructor;
import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/20/2017 (3:09 PM)
 */
@Focus ( "modify-motd" )
@AllArgsConstructor
public class ModifyMOTDPayload implements Payload
{

    /**
     * The second line of the MOTD.
     * <p>
     * This is raw input; the proxy will handle parsing.
     * <p>
     * Example text: {@code &cHello}
     */
    public final String line;

    /**
     * The new maximum amount of players allowed on the network.
     */
    public final int max;

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder().add("line", line)
                                .add("max", max)
                                .done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
