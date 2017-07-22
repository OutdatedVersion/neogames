package net.neogamesmc.common.text;

import net.neogamesmc.common.regex.Regex;
import org.apache.commons.lang3.text.WordUtils;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/18/2017 (10:50 PM)
 */
public class Text
{

    /**
     * Currency formatter instance.
     */
    private static final NumberFormat FORMAT = NumberFormat.getInstance(Locale.US);

    /**
     * Pattern to match dashes.
     */
    private static final Pattern DASH = Pattern.compile("-");

    /**
     * Turns the provided enumerator into
     * a human-friendly text version.
     *
     * @param val The enum
     * @return The text
     */
    public static String fromEnum(Enum val)
    {
        return WordUtils.capitalizeFully(
                    val.name()
                    // usually declared all upper-case
                    .toLowerCase()
                    // remove delimiter
                    .replaceAll("_", "")
        );
    }

    /**
     * Turns the provided number into a
     * formatted string.
     *
     * @param val The number
     * @return The text
     */
    public static String fromCurrency(int val)
    {
        return FORMAT.format(val);
    }

    /**
     * Turn the provided array into text.
     *
     * @param array The array
     * @return The single line of text.
     */
    public static String convertArray(String[] array)
    {
        return convertArray(array, 0);
    }

    /**
     * Turn the provided array into a single string.
     *
     * @param array The array
     * @param cutOff When to cutoff
     * @return The single line of text
     */
    public static String convertArray(String[] array, int cutOff)
    {
        final StringBuilder builder = new StringBuilder();

        for (int i = cutOff; i != array.length; i++)
        {
            builder.append(array[i]).append(" ");
        }

        return builder.toString().trim();
    }

    /**
     * Remove all numbers from the provided text.
     *
     * @param in The text
     * @return Stripped text
     */
    public static String stripNumbers(String in)
    {
        return in.replaceAll("[0-9]", "");
    }

    /**
     * Extract the protocol (http/s) from a web address.
     *
     * @param address The address
     * @return The stripped text
     * @see Regex#URL_PROTOCOL Regex to find the protocol
     */
    public static String stripProtocol(String address)
    {
        return Regex.URL_PROTOCOL.matcher(address).replaceAll("");
    }

    /**
     * Remove the dashes from a string representation of a {@link UUID}.
     *
     * @param uuid The UUID being stripped
     * @return The textual version
     */
    public static String stripUUID(UUID uuid)
    {
        checkNotNull(uuid, "You must provide a UUID");

        return DASH.matcher(uuid.toString()).replaceAll("");
    }

    /**
     * Parse a textual representation of a {@link UUID} into an actual UUID object.
     *
     * @param in The raw text
     * @return The UUID
     */
    public static UUID parseUndashedUUID(String in)
    {
        return new UUID(Long.parseUnsignedLong(in.substring(0, 16), 16),
                        Long.parseUnsignedLong(in.substring(16), 16));
    }

    /**
     * Parse the provided text as a UUID. If it follows the standard pattern
     * then use the built in function; if it follows the Mojangian formation
     * then use our own solution for parsing.
     *
     * @param in The text
     * @return The UUID
     */
    public static UUID parseUUID(String in)
    {
        return in.contains("-") ? UUID.fromString(in) : parseUndashedUUID(in);
    }

}
