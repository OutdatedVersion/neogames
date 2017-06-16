package net.neogamesmc.common.login;

import java.util.UUID;

/**
 * Represents a player's attempt to
 * join the network. The ability to
 * decide the outcome of said event
 * is exposed here as well.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/02/2017 (1:51 PM)
 */
public class LoginRequest
{

    /**
     * The player's UUID.
     */
    private final UUID uuid;

    /**
     * The player's username/IP address.
     */
    private final String name, address;

    /**
     * Whether or not this request has been denied.
     */
    private boolean disallow;

    /**
     * If so, the reason for doing so.
     */
    private String denyReason;

    /**
     * Class Constructor
     *
     * @param uuid The player's UUID
     * @param name The player's name
     * @param address The player's IP address
     */
    public LoginRequest(UUID uuid, String name, String address)
    {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
    }

    /**
     * Grabs the player's UUID.
     *
     * @return Their UUID
     */
    public UUID uuid()
    {
        return uuid;
    }

    /**
     * Grabs the player's username.
     *
     * @return Their name
     */
    public String name()
    {
        return name;
    }

    /**
     * Grabs the player's IP address.
     *
     * @return Their IP
     */
    public String address()
    {
        return address;
    }

    /**
     * Returns whether or not this request
     * to login has been denied.
     *
     * @return Yes or no
     */
    public boolean isDenied()
    {
        return disallow;
    }

    /**
     * Grab the reason that the login
     * was denied, in the case it was.
     *
     * @return The reason or {@code null}
     */
    public String denyReason()
    {
        return denyReason;
    }

    /**
     * Prevent the request from
     * falling through.
     *
     * @param reason The reason for denying the
     *               login; Be sure to provide a
     *               DETAILED reason.
     * @return This request, for chaining
     */
    public LoginRequest deny(String reason)
    {
        this.disallow |= true;
        this.denyReason = reason;

        return this;
    }

}
