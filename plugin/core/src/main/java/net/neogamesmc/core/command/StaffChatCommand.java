package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.payload.StaffChatPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.message.Message;
import net.neogamesmc.core.message.option.format.Color;
import net.neogamesmc.core.player.Players;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (4:56 AM)
 */
public class StaffChatCommand
{

    /**
     * Locally held copy of our Redis handler.
     */
    private RedisHandler redis;

    /**
     * Locally held database instance.
     */
    private Database database;

    /**
     * Locally held configuration data.
     */
    private ServerConfiguration config;

    @Inject
    public StaffChatCommand(RedisHandler redis, Database database, ServerConfiguration config)
    {
        this.redis = redis.registerHook(this);
        this.database = database;
        this.config = config;
    }

    /**
     * Expose a method to send out messages across the network to staff members.
     *
     * @param player The player running the command
     * @param message The message to send
     */
    @Command ( executor = { "sc", "staffchat" } )
    @Permission ( value = Role.MOD, note = "Regular chat is just swell. :)" )
    public void run(Player player, @Necessary ( "Please provide a message" ) String[] message)
    {
        new StaffChatPayload(config.name, player.getName(), database.cacheFetch(player.getUniqueId()).role(), Text.convertArray(message)).publish(redis);
    }

    /**
     * Parse and display the provided payload to staff members on this server.
     *
     * @param payload The payload
     */
    @HandlesType ( StaffChatPayload.class )
    public void display(StaffChatPayload payload)
    {
        Players.stream(Role.MOD, database).forEach(entry ->
            entry.getLeft().sendMessage(Message.start().content("Staff Chat", Color.WHITE).bold(true)
                    .append(" " + Text.fromEnum(payload.role) + " " + payload.name, NONE).color(payload.role.color)
                    .append(" " + payload.message).color(ChatColor.YELLOW).create())
        );
    }

}
