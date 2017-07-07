package net.neogamesmc.common.payload;

import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

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
     * Request to remove a server from the network.
     *
     * @param name Name of the server.
     */
    public UpdateNetworkServersPayload(String name)
    {
        this.name = name;
        this.add = false;
    }

    /**
     * Class Constructor
     *
     * @param name Server ID
     * @param port Server port
     */
    public UpdateNetworkServersPayload(String name, int port)
    {
        this.name = name;
        this.port = port;
        this.add = true;
    }

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder().add("name", name).add("port", port).add("add", add).done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.NETWORK;
    }

}
