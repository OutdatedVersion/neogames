package net.neogamesmc.common.text;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/18/2017 (10:50 PM)
 */
public class Text
{

    public static String fromEnum(Enum val)
    {
        final String raw = val.name()
                            // usually declared all upper-case
                            .toLowerCase()
                            // remove delimiter
                            .replaceAll("_", "");

        return raw;
    }

}
