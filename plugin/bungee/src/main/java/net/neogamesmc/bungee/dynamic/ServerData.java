package net.neogamesmc.bungee.dynamic;

import lombok.Data;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (6:59 PM)
 */
@Data
public class ServerData
{

    /**
     * ID of the server.
     */
    public final int id;

    /**
     * Name of the server.
     */
    public final String name;

    /**
     * Group this server is in.
     */
    public final String group;

    /**
     * Port of the server.
     */
    public final int port;

    /**
     * The maximum amount of players allowed on this server.
     */
    public final int maxPlayers;

}
