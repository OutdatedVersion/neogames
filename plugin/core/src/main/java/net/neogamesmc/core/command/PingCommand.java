package net.neogamesmc.core.command;

import lombok.val;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.message.Message;
import net.neogamesmc.core.message.option.format.Color;
import net.neogamesmc.core.player.Players;
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
        val target = targetName == null ? player : Players.find(player, targetName, true);

        if (target != null)
            Message.prefix("Latency").content("The latency of").player(target).content("is observed as").content(((CraftPlayer) target).getHandle().ping + "ms", Color.GREEN).send(player);
    }

}
