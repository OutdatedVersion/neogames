package net.neogamesmc.core.command;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import lombok.val;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.common.payload.RawSwitchServerPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.message.Message;
import net.neogamesmc.core.message.option.format.Color;
import net.neogamesmc.core.player.Players;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/08/2017 (12:53 AM)
 */
public class SendPlayersCommand
{

    /**
     * Local server data hold.
     */
    @Inject private ServerConfiguration config;

    /**
     * Locally held Redis instance.
     */
    @Inject private RedisHandler redis;

    @Command ( executor = "send" )
    @Permission ( Role.ADMIN )
    public void run(Player player,
                    @Necessary ( "Please provide targets to send" ) String targets,
                    @Necessary ( "Please provide a target server" ) String server)
    {
        // Parse input
        val target = targets.contains(",") ? targets.split(",") :
                     targets.equalsIgnoreCase("all") ? everyone() : new String[] { targets };

        if (server.equalsIgnoreCase("here"))
            server = config.name;

        // Move servers
        new RawSwitchServerPayload(server, target).publish(redis);

        // Inform
        Message.prefix("Send").content("Forcing").content(target.length, Color.GREEN)
        .content("player" + (target.length > 1 ? "s" : "") + " to").content(server, Color.YELLOW).send(player);
    }

    /**
     * Returns a array with everyone online player's name in it.
     *
     * @return The array
     */
    private static String[] everyone()
    {
        return Joiner.on(",").join(Players.stream().map(Player::getName).collect(Collectors.toSet())).split(",");
    }

}
