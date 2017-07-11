package net.neogamesmc.common.regex;

import java.util.regex.Pattern;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/10/2017 (10:13 PM)
 */
public final class Regex
{

    /**
     * Pattern to match web addresses.
     */
    public static final Pattern URL = Pattern.compile("^(?:(https?)://)?([-\\w_.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    /**
     * Pattern to match the protocol (http/s) in web addresses.
     */
    public static final Pattern URL_PROTOCOL = Pattern.compile("(https?://)");

}
