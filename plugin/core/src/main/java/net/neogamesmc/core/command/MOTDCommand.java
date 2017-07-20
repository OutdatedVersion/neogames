package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.neogamesmc.common.payload.ModifyMOTDPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.command.api.annotation.SubCommand;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/20/2017 (3:22 PM)
 */
public class MOTDCommand
{

    /**
     * Local Redis instance.
     */
    @Inject private RedisHandler redis;

    @Command ( executor = "motd" )
    @Permission ( Role.ADMIN )
    public void baseCommand(Player player)
    {
        // TODO(Ben): help system
        Message.prefix("Ping Response").content("Invalid usage", ChatColor.RED).send(player);
    }

    @SubCommand ( of = "motd", executors = "max" )
    @Permission ( Role.ADMIN )
    public void updatePlayerCount(Player player, Integer count)
    {
        new ModifyMOTDPayload(null, count).publish(redis);

        Message.prefix("Ping Response").content("Updated max player count to ").content(count, ChatColor.YELLOW).send(player);
    }

    @SubCommand ( of = "motd", executors = "line" )
    @Permission ( Role.ADMIN )
    public void updateSecondLine(Player player, String val)
    {
        new ModifyMOTDPayload(val, -1).publish(redis);

        Message.prefix("Ping Response")
                .content("Updated line to [")
                .content(ChatColor.translateAlternateColorCodes('&', val))
                .content("]", ChatColor.GRAY)
                .send(player);
    }

}
