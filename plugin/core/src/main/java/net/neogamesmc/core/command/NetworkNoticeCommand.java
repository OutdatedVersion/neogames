package net.neogamesmc.core.command;

import com.google.inject.Inject;
import lombok.val;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.common.payload.NetworkNoticePayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.player.Players;
import net.neogamesmc.core.text.Message;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.YELLOW;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (3:51 AM)
 */
public class NetworkNoticeCommand
{

    /**
     * Data of this server.
     */
    @Inject private ServerConfiguration config;

    /**
     * Local copy of our Redis instance.
     */
    private RedisHandler redis;

    /**
     * Class Constructor
     *
     * @param redis Our Redis handler
     */
    @Inject
    public NetworkNoticeCommand(RedisHandler redis)
    {
        this.redis = redis.registerHook(this);
    }

    /**
     * Expose method to send out these notifications.
     *
     * @param player The player
     * @param targetServer The target
     * @param message The message
     */
    @Command ( executor = { "notif", "notice", "notification" } )
    @Permission ( Role.ADMIN )
    public void run(Player player,
                    @Necessary ( "Please provide target servers. (Use 'all' to send it everywhere)" ) String targetServer,
                    @Necessary ( "Please supply a message" ) String[] message)
    {
        new NetworkNoticePayload(parseTarget(targetServer), Text.convertArray(message)).publish(redis);
        Message.prefix("Network").content("Now propagating network notification", YELLOW).send(player);
    }

    /**
     * Handle the displaying of the payload.
     *
     * @param payload The payload
     */
    @HandlesType ( NetworkNoticePayload.class )
    public void display(NetworkNoticePayload payload)
    {
        if (matchesThisServer(payload))
        {
            // Construct
            val message = Message.start().content("Network Notification ").color(AQUA).bold(true)
                                      .content(payload.message).bold(false).color(YELLOW).create();

            // Send out
            Players.stream().forEach(player ->
            {
                player.sendMessage(message);
                Players.sound(player, Sound.ENTITY_WITHER_SPAWN);
            });
        }
    }

    /**
     * Check if the provided payload matches this server.
     *
     * @param payload The payload
     * @return Yes or no
     */
    private boolean matchesThisServer(NetworkNoticePayload payload)
    {
        if (payload.toAll())
            return true;

        boolean matches = false;

        for (String target : payload.targetServers)
        {
            if (target.contains("*"))
                matches = config.name.matches(target);
            else
                matches = config.name.equalsIgnoreCase(target);

            // Already does, no need to process more
            if (matches) break;
        }

        return matches;
    }

    /**
     * Parse the target server input from our command.
     *
     * @param in The text
     * @return The array
     */
    private static String[] parseTarget(String in)
    {
        return in.contains(",") ? in.split(",") : new String[] { in };
    }

}
