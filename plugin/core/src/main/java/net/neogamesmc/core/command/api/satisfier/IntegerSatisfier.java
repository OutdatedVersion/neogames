package net.neogamesmc.core.command.api.satisfier;

import net.neogamesmc.core.command.api.ArgumentSatisfier;
import net.neogamesmc.core.command.api.Arguments;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.ChatColor.RED;
import static net.md_5.bungee.api.ChatColor.YELLOW;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/20/2017 (3:23 PM)
 */
public class IntegerSatisfier implements ArgumentSatisfier<Integer>
{

    @Override
    public Integer get(Player player, Arguments args)
    {
        try
        {
            return Integer.parseInt(args.next());
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    @Override
    public String fail(String provided)
    {
        return "Could not parse [" + YELLOW + provided + RED + "]";
    }

    @Override
    public Class<Integer> satisfies()
    {
        return Integer.class;
    }

}
