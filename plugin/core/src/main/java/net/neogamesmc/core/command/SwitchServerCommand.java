package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.neogamesmc.common.backend.SwitchServerPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.ChatColor.YELLOW;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (12:45 AM)
 */
public class SwitchServerCommand
{

    /**
     * Work with our Redis instance.
     */
    @Inject private RedisHandler redis;

    @Command ( executor = "server" )
    @Permission ( value = "core.command.server", note = "Sorry, you're not permitted to use this! Try the menus/NPCs to get around! :)" )
    public void execute(Player player, @Necessary ( "You missed the server's name" ) String server)
    {
        // TODO(Ben): move this
        Message.prefix("Network").content("You are being connected to:").content(server, YELLOW).send(player);
        new SwitchServerPayload(server, player.getUniqueId().toString()).publish(redis);
    }

    @Command ( executor = { "current", "where", "whereami" } )
    public void execute(Player player)
    {
        Message.prefix("Network").content("You are currently connected to:").content("require-inject", YELLOW).send(player);
    }

}
