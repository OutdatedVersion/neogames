package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.common.payload.FindAndSwitchServerPayload;
import net.neogamesmc.common.payload.RawSwitchServerPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.ChatColor.RED;
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

    /**
     * Data associated with this server.
     */
    @Inject private ServerConfiguration data;

    /**
     * Expose a method for staff members to join exact servers.
     *
     * @param player The player to send
     * @param server The server to send them to
     */
    @Command ( executor = { "server", "s" } )
    @Permission ( value = Role.MOD, note = "Try using the menus/NPCs to get around! :)" )
    public void serverCommand(Player player, @Necessary ( "You missed the server's name" ) String server)
    {
        if (server.equalsIgnoreCase(data.name))
        {
            Message.prefix("Network").content("You are already connected to that server", RED).send(player);
            return;
        }

        new RawSwitchServerPayload(server, player.getUniqueId().toString()).publish(redis);
    }

    /**
     * Expose a method to players for viewing the name of the server they're currently connected to.
     *
     * @param player The player who ran the command
     */
    @Command ( executor = { "current", "where", "whereami" } )
    public void whereCommand(Player player)
    {
        Message.prefix("Network").content("You are currently connected to:").content(data.name, YELLOW).send(player);
    }

    /**
     * Provide a method to send players out to lobbies.
     *
     * @param player The player running this command
     */
    @Command ( executor = { "lobby", "hub", "leave" } )
    public void lobbyCommand(Player player)
    {
        if (data.name.startsWith("lobby"))
        {
            Message.prefix("Network").content("You are already connected to a lobby!", RED).sendAsIs(player);
            return;
        }

        new FindAndSwitchServerPayload("lobby", player.getUniqueId().toString()).publish(redis);
    }

}
