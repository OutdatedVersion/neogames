package net.neogamesmc.core.command.api.satisfier;

import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.ArgumentSatisfier;
import net.neogamesmc.core.command.api.Arguments;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

/**
 * @author Ben (OutdatedVersion)
 * @since Mar/21/2017 (8:37 PM)
 */
public class RoleSatisfier implements ArgumentSatisfier<Role>
{

    @Override
    public Role get(Player player, Arguments args)
    {
        try
        {
            return Role.valueOf(args.next().toUpperCase());
        }
        catch (IllegalArgumentException ex)
        {
            return null;
        }
    }

    @Override
    public String fail(String provided)
    {
        return "No role matching [" + GREEN + provided.toUpperCase() + GRAY + "].";
    }

    @Override
    public Class<Role> satisfies()
    {
        return Role.class;
    }

}
