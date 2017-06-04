package com.neomccreations.common.login;

import com.google.common.collect.Sets;
import com.neomccreations.common.database.operation.FetchOperation;

import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/02/2017 (1:51 PM)
 */
public class LoginRequest
{

    private final UUID uuid;
    private final String name, address;

    public LoginRequest(UUID uuid, String name, String address)
    {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
        this.results = Sets.newConcurrentHashSet();
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

    public <T> LoginRequest deciding(FetchOperation fetch, Class<T> clazz, Function<T, LoginResult> dataToResult)
    {

    }

}
