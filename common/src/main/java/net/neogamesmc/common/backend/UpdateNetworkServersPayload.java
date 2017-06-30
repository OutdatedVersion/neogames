package net.neogamesmc.common.backend;

import com.google.gson.JsonObject;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (1:51 AM)
 */
@Focus ( "update-network-servers" )
public class UpdateNetworkServersPayload implements Payload
{

    /**
     * The server's ID.
     */
    public String name;

    /**
     * The port of the server.
     * <p>
     * May be {@code null}
     */
    public int port;

    /**
     * If {@code true} then we'll add the
     * desired server to the proxy. If not,
     * we'll assume that we will be removing
     * one following the property of: {@link #name}
     */
    public boolean add;

    /**
     * Class Constructor
     *
     * @param name Server ID
     * @param port Server port
     * @param add Whether or not to add it
     */
    public UpdateNetworkServersPayload(String name, int port, boolean add)
    {
        this.name = name;
        this.port = port;
        this.add = add;
    }

    @Override
    public JsonObject asJSON()
    {
        final JsonObject json = new JsonObject();

        json.addProperty("name", name);
        json.addProperty("port", port);
        json.addProperty("add", add);

        return json;
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
