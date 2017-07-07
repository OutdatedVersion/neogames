package net.neogamesmc.network.api;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/06/2017 (11:31 PM)
 */
@AllArgsConstructor
@NoArgsConstructor
public class ConnectedServer
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

    /**
     * The total amount of players currently on this server.
     */
    public int onlinePlayers;

    /**
     * The top-bound to players allowed on this server.
     */
    public int maxPlayers;

}
