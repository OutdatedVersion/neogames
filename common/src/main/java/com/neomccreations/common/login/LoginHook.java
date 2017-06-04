package com.neomccreations.common.login;

/**
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/02/2017 (1:51 PM)
 */
public interface LoginHook
{

    LoginResult processLogin(LoginRequest request);

}
