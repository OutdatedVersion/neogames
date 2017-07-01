package net.neogamesmc.common.backend;

import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collections;

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
    public SwitchServerPayload(String server, String... targets)
    {
        this.server = server;
        this.targets = targets;
        this.bulk = targets.length > 1;
    }

    @Override
    public JSONObject asJSON()
    {
        final JSONObject json = new JSONObject();
        final JSONArray targetsJSON = new JSONArray();
        Collections.addAll(targetsJSON, this.targets);

        json.put("bulk", bulk);
        json.put("server", server);
        json.put("targets", targetsJSON);

        return json;
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
