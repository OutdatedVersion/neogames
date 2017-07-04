package net.neogamesmc.common.payload;

import lombok.RequiredArgsConstructor;
import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import net.neogamesmc.common.reference.Role;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (6:26 PM)
 */
@Focus ( "update-player-role" )
@RequiredArgsConstructor
public class UpdatePlayerRolePayload implements Payload
{

    /**
     * Name of the player.
     */
    public final String name;

    /**
     * The player's role.
     */
    public final Role role;

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder().add("name", name).add("role", role).done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
