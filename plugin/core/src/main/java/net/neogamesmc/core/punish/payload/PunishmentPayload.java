package net.neogamesmc.core.punish.payload;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import net.neogamesmc.core.punish.PunishmentType;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (6:41 AM)
 */
@Focus ( "punishment" )
public class PunishmentPayload implements Payload
{

    /**
     * The punishment's ID.
     */
    public int id;

    /**
     * The type of punishment this is referencing.
     */
    public PunishmentType type;

    /**
     * The reason behind issuing this punishment.
     */
    public String[] reason;

    /**
     * The name of the player it's being issued against.
     */
    @SerializedName ( "target" )
    public String targetPlayer;

    /**
     * UNIX epoch timestamp of when this expires
     */
    @SerializedName ( "expires_at" )
    public long expiresAt;

    /**
     * Class Constructor
     *
     * @param id The ID
     * @param type The type
     * @param reason The reason
     * @param targetPlayer The target
     * @param expiresAt When it expires
     */
    public PunishmentPayload(int id, PunishmentType type, String targetPlayer, long expiresAt, String[] reason)
    {
        this.id = id;
        this.type = type;
        this.reason = reason;
        this.targetPlayer = targetPlayer;
        this.expiresAt = expiresAt;
    }

    @Override
    public JsonObject asJSON()
    {
        return new JSONBuilder().add("id", id)
                                .add("type", type.name())
                                .add("reason", reason)
                                .add("target", targetPlayer)
                                .add("expires_at", expiresAt)
                                .done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
