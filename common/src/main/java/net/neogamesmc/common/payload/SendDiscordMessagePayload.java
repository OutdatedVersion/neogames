package net.neogamesmc.common.payload;

import lombok.AllArgsConstructor;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/02/2017 (12:21 AM)
 */
@Focus ( "discord-message" )
@AllArgsConstructor
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

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
