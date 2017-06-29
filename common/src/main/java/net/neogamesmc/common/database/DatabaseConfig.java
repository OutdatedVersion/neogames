package net.neogamesmc.common.database;

import net.neogamesmc.common.annotation.Data;

import java.util.function.Function;

import static java.lang.String.format;

/**
 * Holds values required by our database.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (9:54 PM)
 */
@Data
public class DatabaseConfig
{

    /**
     * Takes in our {@link DatabaseConfig} and returns a MySQL friendly connection address.
     */
    public static final Function<DatabaseConfig, String> FORMAT_JDBC_URL = config -> format("jdbc:mysql://%s:%s/%s",
                                                                                            config.host, config.port, config.database);

    /**
     * Info regarding authentication.
     */
    public AuthInfo auth;

    /**
     * The hostname of the server.
     * <p>
     * Default to local server.
     */
    public String host = "127.0.0.1";

    /**
     * The port of the server.
     */
    public int port = 3306;

    /**
     * The specific database we'll be using.
     */
    public String database;

    /**
     * Holds details relating to
     * logging into servers.
     */
    public static class AuthInfo
    {
        public String username;
        public String password;
    }

}
