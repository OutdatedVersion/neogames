package net.neogamesmc.core.punish;

import net.neogamesmc.common.time.TimeFormatting;
import net.neogamesmc.common.payload.PunishmentPayload;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

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
     * Returns a pretty-format of when the
     * represented punishment expires.
     *
     * @param payload Payload of the punishment
     * @return The formatted text
     */
    static String format(PunishmentPayload payload)
    {
        return payload.expiresAt == -1 ? "Never ending sentence" : TimeFormatting.format(Instant.ofEpochMilli(payload.expiresAt));
    }

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
