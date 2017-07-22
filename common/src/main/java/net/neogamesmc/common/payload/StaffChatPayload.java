package net.neogamesmc.common.payload;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import net.neogamesmc.common.reference.Role;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (5:24 AM)
 */
@Focus ( "staff-chat" )
@AllArgsConstructor
public class StaffChatPayload implements Payload
{

    /**
     * Name of the server this message was sent on.
     */
    @SerializedName ( "sent_on" )
    public String sentOn;

    /**
     * Name of the player who sent this message.
     */
    public String name;

    /**
     * Role of the player who sent the message.
     */
    public Role role;

    /**
     * The message.
     */
    public String message;

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
