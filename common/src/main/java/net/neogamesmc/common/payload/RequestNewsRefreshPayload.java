package net.neogamesmc.common.payload;

import com.google.gson.annotations.SerializedName;
import lombok.RequiredArgsConstructor;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (12:18 PM)
 */
@Focus ( "req-news-refresh" )
@RequiredArgsConstructor
public class RequestNewsRefreshPayload implements Payload
{

    /**
     * ID of the line being updated.
     */
    public final int id;

    /**
     * Username of the player who did this.
     */
    @SerializedName ( "updated_by" )
    public final String updatedBy;

    /**
     * The updated line content.
     */
    @SerializedName ( "new_value" )
    public final String newValue;

    /**
     * When this payload was published basically.
     */
    @SerializedName ( "updated_at" )
    public final long updatedAt;

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
