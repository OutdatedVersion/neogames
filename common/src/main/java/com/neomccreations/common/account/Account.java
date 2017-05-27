package com.neomccreations.common.account;

import com.neomccreations.common.database.annotation.Column;
import com.neomccreations.common.database.annotation.InheritColumn;
import com.neomccreations.common.database.annotation.Table;
import com.neomccreations.common.reference.Role;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Representation of a player's data.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (10:06 PM)
 */
@Table ( "accounts" )
public class Account
{

    /**
     * Constant uniquely assigned identifier.
     */
    @InheritColumn
    public int id;

    /**
     * Constant Unique ID assigned by Mojang
     * to distinguish players.
     */
    @InheritColumn
    public UUID uuid;

    /**
     * Player chosen display name.
     */
    @InheritColumn
    public String name;

    /**
     * Permission/display role of this player.
     */
    @InheritColumn
    public Role role;

    /**
     * UNIX epoch timestamp of when the player was first seen here.
     */
    @Column ( "first_login" )
    public Instant firstLogin;

    /**
     * UNIX epoch timestamp of when the player was last seen on the network.
     */
    @Column ( "last_login" )
    public Instant lastLogin;

    /**
     * The last IP address we saw this player from.
     */
    @Column ( "address" )
    public String ip;

    @Override
    public boolean equals(Object o)
    {
        return this == o || !(o == null || getClass() != o.getClass()) && id == ((Account) o).id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }

}
