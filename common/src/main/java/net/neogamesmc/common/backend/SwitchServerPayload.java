package net.neogamesmc.common.backend;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;

/**
 * Represents the action of changing the
 * server that a player is connected to.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (1:51 AM)
 */
@Focus ( "switch-server" )
public class SwitchServerPayload implements Payload
{

    /**
     * A group of every player that will be sent
     * via this payload.
     * <p>
     * Every element in this may be either a
     * {@link java.util.UUID} or their username.
     */
    public String[] targets;

    /**
     * The server to send the players to.
     */
    public String server;

    /**
     * Whether or not we're sending multiple
     * players via one request.
     */
    public boolean bulk;

    /**
     * Class Constructor
     *
     * @param server The nam,e
     * @param targets The players to send
     */
    public SwitchServerPayload(String server, String[] targets)
    {
        this.server = server;
        this.targets = targets;
        this.bulk = targets.length > 1;
    }

    @Override
    public JsonObject asJSON()
    {
        final JsonObject json = new JsonObject();
        final JsonArray targets = new JsonArray();

        for (String element : this.targets)
            targets.add(element);

        json.addProperty("bulk", bulk);
        json.add("targets", targets);

        return json;
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
