package net.neogamesmc.common.payload;

import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/08/2017 (2:22 PM)
 */
@Focus ( "queue-player" )
public class QueuePlayersForGroupPayload implements Payload
{

    public final String group;
    public final String[] targets;

    public QueuePlayersForGroupPayload(String group, String... targets)
    {
        this.group = group;
        this.targets = targets;
    }

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder().add("targets", targets).add("group", group).done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
