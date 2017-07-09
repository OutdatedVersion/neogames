package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.payload.StaffChatPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.annotation.Permission;
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

    @Command ( executor = { "s", "c", "staffchat" } )
    @Permission ( value = Role.MOD, note = "Regular chat works fine. :)" )
    public void run(Player player, String[] message)
    {
        new StaffChatPayload(config.name, player.getName(), database.cacheFetch(player.getUniqueId()).role(), Text.convertArray(message)).publish(redis);
    }

    @HandlesType ( StaffChatPayload.class )
    public void display(StaffChatPayload payload)
    {
        Players.stream(Role.MOD, database).forEach(entry ->
            entry.getLeft().sendMessage(new ComponentBuilder("Staff Chat").bold(true)
                    .append(Text.fromEnum(entry.getRight()) + " " + entry.getLeft().getName(), NONE).color(entry.getRight().color)
                    .append(payload.message).color(ChatColor.YELLOW).create())
        );
    }

}
