package net.neogamesmc.common.time;

import org.ocpsoft.prettytime.PrettyTime;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/02/2017 (11:16 PM)
 */
public class TimeFormatting
{

    /**
     * Instance of the library used to do this formatting.
     */
    private static final PrettyTime PRETTY_TIME = new PrettyTime(Locale.ENGLISH);

    /**
     * Format the provided time into a human
     * readable version of itself.
     *
     * @param instant The date/time
     * @return The formatted text
     */
    public static String format(Instant instant)
    {
        return PRETTY_TIME.format(Date.from(instant));
    }

    /**
     * Format the provided time into a long
     * human readable version.
     *
     * @param instant The date/time
     * @return The formatted text
     */
    public static String formatLong(Instant instant)
    {
        return PRETTY_TIME.format(PRETTY_TIME.calculatePreciseDuration(Date.from(instant)));
    }

}
