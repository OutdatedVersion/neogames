package net.neogamesmc.core.command.api.satisfier;

import net.neogamesmc.core.command.api.ArgumentSatisfier;
import net.neogamesmc.core.command.api.Arguments;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Mar/25/2017 (7:01 PM)
 */
public class StringArraySatisfier implements ArgumentSatisfier<String[]>
{

    @Override
    public String[] get(Player player, Arguments args)
    {
        final String[] array = new String[args.remainingElements()];

        for (int i = args.currentPosition(); i < array.length; i++)
        {
            array[i] = args.next();
        }

        return array;
    }

    @Override
    public String fail(String provided)
    {
        return "Unable to provision array... but how?";
    }

    @Override
    public Class<String[]> satisfies()
    {
        return String[].class;
    }

}
