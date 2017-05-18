package com.neomccreations.common.account;

import com.neomccreations.common.reference.Role;

import java.util.UUID;

/**
 * Representation of a player's data.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (10:06 PM)
 */
public class Account
{

    /**
     * Constant uniquely assigned identifier.
     */
    public int id;

    /**
     * Constant Unique ID assigned by Mojang
     * to distinguish players.
     */
    public UUID uuid;

    /**
     * Player chosen display name.
     */
    public String name;

    /**
     * Permission/display role of this player.
     */
    public Role role;

    /**
     * UNIX epoch timestamp of when the player was first seen here.
     */
    public long firstLogin;

    /**
     * UNIX epoch timestamp of when the player was last seen on the network.
     */
    public long lastLogin;

    /**
     * The last address we saw this player from.
     */
    public String ip;

}
