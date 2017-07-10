package net.neogamesmc.common.text;

import org.apache.commons.lang3.text.WordUtils;

import java.text.NumberFormat;
import java.util.Locale;

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

}
