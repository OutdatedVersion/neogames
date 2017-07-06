package net.neogamesmc.network.data;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (4:00 AM)
 */
public class ServerData
{

    /**
     * A unique identifier for this server.
     */
    public int id;

    /**
     * The name of this server.
     * <p>
     * Example: {@code lobby1}
     */
    public String name;

    /**
     * The port this server is on.
     */
    public int port;

    /**
     * Where this server is located.
     * <p>
     * Follows: {@code /mc/network/live/{server_id}}
     */
    public String dir;

    /**
     * The ID of the server group we're in.
     */
    public String group;

}
