package net.neogamesmc.core.command;

import net.md_5.bungee.api.ChatColor;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.player.Players;
import net.neogamesmc.core.text.Message;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/10/2017 (4:53 PM)
 */
public class PingCommand
{

    /**
     * Expose a method to allow you to view your own ping.
     *
     * @param player The player who ran the command
     */
    @Command ( executor = { "ping", "lag" } )
    public void run(Player player, String targetName)
    {
        Player target = targetName == null ? player : Players.find(player, targetName, true);

        if (target != null)
            Message.prefix("Latency").content("The latency of").player(target).content("is observed as").content(((CraftPlayer) player).getHandle().ping + "ms", ChatColor.GREEN).send(player);
    }

}
