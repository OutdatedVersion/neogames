package net.neogamesmc.common.backend;

import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/02/2017 (12:21 AM)
 */
@Focus ( "discord-message" )
public class SendDiscordMessagePayload implements Payload
{

    /**
     * The channel ID to send it to.
     */
    public final long channel;

    /**
     * The message content itself to send.
     */
    public final String content;

    /**
     * Class Constructor
     *
     * @param channel The channel's ID.
     * @param content The content
     */
    public SendDiscordMessagePayload(long channel, String content)
    {
        this.channel = channel;
        this.content = content;
    }

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder().add("channel", channel).add("content", content).done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
