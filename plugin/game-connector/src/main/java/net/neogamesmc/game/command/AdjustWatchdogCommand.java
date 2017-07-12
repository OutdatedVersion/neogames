package net.neogamesmc.game.command;

import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.annotation.Permission;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/11/2017 (4:10 PM)
 */
public class AdjustWatchdogCommand
{

    @Command ( executor = "capacitywatchdog" )
    @Permission ( Role.ADMIN )
    public void run(Player player)
    {

    }

}
