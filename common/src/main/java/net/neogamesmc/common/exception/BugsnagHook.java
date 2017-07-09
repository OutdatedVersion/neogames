package net.neogamesmc.common.exception;

import com.bugsnag.Bugsnag;
import net.neogamesmc.common.config.ConfigurationProvider;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (4:47 AM)
 */
public class BugsnagHook
{

    /**
     * Bugsnag wrapper instance.
     */
    private static final Bugsnag WRAPPER = new Bugsnag(new ConfigurationProvider().read("backend/bugsnag", Config.class).token);

    /**
     * Send a report out.
     *
     * @param throwable The issue
     */
    public static void report(Throwable throwable)
    {
        WRAPPER.notify(throwable);
    }

    /**
     * Configuration for this.
     */
    public static class Config
    {
        String token;
    }


}
