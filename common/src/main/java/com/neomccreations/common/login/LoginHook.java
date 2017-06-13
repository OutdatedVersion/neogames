package com.neomccreations.common.login;

/**
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/02/2017 (1:51 PM)
 */
public interface LoginHook
{

    /**
     * Fetch data from the login and/or
     * determine the {@link LoginResult.Outcome} of the login.
     *
     * @param request Representation of the player's login
     */
    void processLogin(LoginRequest request);

}
