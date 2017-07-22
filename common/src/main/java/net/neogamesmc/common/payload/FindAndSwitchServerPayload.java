package net.neogamesmc.common.payload;

import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (5:48 AM)
 */
@Focus ( "req-server-change" )
public class FindAndSwitchServerPayload implements Payload
{

    /**
     * The group the player is requesting.
     */
    public String group;

    /**
     * UUIDs of the players to switch out.
     */
    public String[] targets;

    /**
     * Class Constructor
     *
     * @param group The target group
     * @param targets The targets
     */
    public FindAndSwitchServerPayload(String group, String... targets)
    {
        this.group = group;
        this.targets = targets;
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
