package net.neogamesmc.common.payload;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (6:41 AM)
 */
@Focus ( "punishment" )
@AllArgsConstructor
public class PunishmentPayload implements Payload
{

    /**
     * The punishment's ID.
     */
    public int id;

    /**
     * The type of punishment this is referencing.
     */
    public String type;

    /**
     * The name of the player it's being issued against.
     */
    @SerializedName ( "target" )
    public String targetName;

    /**
     * UNIX epoch timestamp of when this expires
     */
    @SerializedName ( "expires_at" )
    public long expiresAt;

    /**
     * The reason behind issuing this punishment.
     */
    public String reason;

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder().add("id", id)
                                .add("type", type)
                                .add("reason", reason)
                                .add("target", targetName)
                                .add("expires_at", expiresAt)
                                .done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
