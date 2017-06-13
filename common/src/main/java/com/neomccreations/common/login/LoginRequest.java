package com.neomccreations.common.login;

import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/02/2017 (1:51 PM)
 */
public class LoginRequest
{

    private final UUID uuid;
    private final String name, address;

    private boolean disallow;
    private String denyReason;

    public LoginRequest(UUID uuid, String name, String address)
    {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
    }

    public UUID uuid()
    {
        return uuid;
    }

    public String name()
    {
        return name;
    }

    public String address()
    {
        return address;
    }

    public boolean isDenied()
    {
        return disallow;
    }

    public String denyReason()
    {
        return denyReason;
    }

    public LoginRequest deny(String reason)
    {
        this.disallow |= true;
        this.denyReason = reason;

        return this;
    }

}
