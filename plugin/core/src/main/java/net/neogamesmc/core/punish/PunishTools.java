package net.neogamesmc.core.punish;

import net.neogamesmc.core.punish.payload.PunishmentPayload;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (7:02 AM)
 */
class PunishTools
{

    /**
     * The amount of milliseconds in one week.
     */
    private static final long WEEK_TO_MS = 604800000L;

    /**
     * The amount of milliseconds in one month.
     */
    private static final long MONTH_TO_MS = 2629746000L;

    /**
     * Format time in a pretty way.
     */
    static final PrettyTime PRETTY_TIME = new PrettyTime(Locale.ENGLISH);

    /**
     * Format the time until a {@link Punishment} expires.
     */
    static final Function<PunishmentPayload, String> FORMAT_LENGTH = data -> PRETTY_TIME.format(new Date(data.expiresAt));

    /**
     * Format the time until a {@link Punishment} expires; the long way.
     * <p>
     * Quite a bit of code with this, so wanted to hold it here.
     */
    static final Function<PunishmentPayload, String> FORMAT_LENGTH_FULL = data -> PRETTY_TIME.format(PRETTY_TIME.calculatePreciseDuration(new Date(data.expiresAt)));

    /**
     * Figure out the proper time via the
     * passed in text.
     *
     * @param in The character
     * @return A unit of time or {@code -1} if no match
     */
    static long parseTime(int amount, char in)
    {
        switch (in)
        {
            case 'd':
                return TimeUnit.DAYS.toMillis(amount);

            case 'h':
                return TimeUnit.HOURS.toMillis(amount);

            case 'm':
                return MONTH_TO_MS * amount;

            case 'w':
                return WEEK_TO_MS * amount;

            default:
                throw new IllegalArgumentException("No unit match found with supplied text: " + in);
        }
    }

}
