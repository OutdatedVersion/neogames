package net.neogamesmc.core.command;

import net.neogamesmc.core.command.api.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (11:21 AM)
 */
public class HelpCommand
{

    @Command ( executor = "help" )
    public void run(Player player)
    {
        player.sendMessage(ChatColor.GREEN + "How do we help you?");
    }

}
