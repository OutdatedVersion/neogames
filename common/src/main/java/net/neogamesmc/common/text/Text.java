package net.neogamesmc.common.text;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/18/2017 (10:50 PM)
 */
public class Text
{

    /**
     * Turns the provided enumerator into
     * a human-friendly text version.
     *
     * @param val The enum
     * @return The text
     */
    public static String fromEnum(Enum val)
    {
        final String raw = val.name()
                            // usually declared all upper-case
                            .toLowerCase()
                            // remove delimiter
                            .replaceAll("_", "");

        return raw;
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

}
