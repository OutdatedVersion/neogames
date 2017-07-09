package net.neogamesmc.common.payload;

import com.google.gson.annotations.SerializedName;
import lombok.RequiredArgsConstructor;
import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (3:57 AM)
 */
@Focus ( "network-notification" )
@RequiredArgsConstructor
public class NetworkNoticePayload implements Payload
{

    /**
     * The servers to display this on. If it should be displayed
     * on every server the only element in the set will be {@code "ALL"}.
     */
    @SerializedName ( "target_servers" )
    public final String[] targetServers;

    /**
     * The message to display.
     */
    public final String message;

    /**
     * Whether or not this is a request to every server.
     *
     * @return Yes or no
     */
    public boolean isAll()
    {
        return targetServers[0].equals("ALL");
    }

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder().add("target_servers", targetServers).add("message", message).done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
