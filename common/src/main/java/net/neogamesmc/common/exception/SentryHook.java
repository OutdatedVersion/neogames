package net.neogamesmc.common.exception;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import net.neogamesmc.common.config.ConfigurationProvider;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (4:47 AM)
 */
public class SentryHook
{

    /**
     * Of main copy of our client.
     */
    private static final SentryClient CLIENT;

    // start it up
    static
    {
        Sentry.init(new ConfigurationProvider().read("backend/sentry", Config.class).dsn);

        CLIENT = SentryClientFactory.sentryClient();
    }

    /**
     * Send a report out to the web-interface.
     *
     * @param throwable The issue
     */
    public static void report(Throwable throwable)
    {
        CLIENT.sendException(throwable);
    }

    /**
     * Configuration for our error tracker tool.
     */
    public static class Config
    {
        String dsn;
    }

}
