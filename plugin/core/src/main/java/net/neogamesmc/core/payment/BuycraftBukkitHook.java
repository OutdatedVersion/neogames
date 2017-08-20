package net.neogamesmc.core.payment;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.payload.TransactionNoticePayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.event.UpdatePlayerRoleEvent;
import net.neogamesmc.core.message.option.format.Color;
import net.neogamesmc.core.player.Players;
import net.neogamesmc.core.message.Message;
import org.bukkit.Bukkit;

import static net.md_5.bungee.api.ChatColor.*;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.FORMATTING;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (2:27 AM)
 */
@Singleton
@ParallelStartup
public class BuycraftBukkitHook
{

    /**
     * Our database instance.
     */
    @Inject private Database database;

    /**
     * Class Constructor
     *
     * @param redis Our Redis instance
     */
    @Inject
    public BuycraftBukkitHook(RedisHandler redis)
    {
        redis.registerHook(this);
    }

    @FromChannel ( RedisChannel.DEFAULT )
    @HandlesType ( TransactionNoticePayload.class )
    public void process(TransactionNoticePayload payload)
    {
        val target = Bukkit.getPlayerExact(payload.name);

        // Global announcement
        val all = Message.prefix("NeoGames").content("Thanks to")
                .player(payload.name)
                .content("for supporting our network at")
                .content("neogamesmc.buycraft.net", Color.RED)
                .content("!", Color.GRAY).create();

        Players.stream().filter(player -> !player.getName().equals(payload.name))
                        .forEach(player -> player.sendMessage(all));

        // Individual notification & apply perks
        if (target != null)
        {
            if (payload.type.equals("ROLE"))
            {
                val account = database.cacheFetch(target.getUniqueId());
                val previous = account.role();

                // Modify local role
                account.unsafeRole(Role.valueOf(payload.data[0]));

                // Call server notification
                new UpdatePlayerRoleEvent(target, previous, account.role()).callEvent();

                target.sendMessage(new ComponentBuilder("                                                                           ").strikethrough(true).color(AQUA)
                                                .append("\nPurchase Received!\n\n", NONE).color(RED).bold(true)
                                                .append("Thanks for grabbing ", NONE).color(GRAY)
                                                .append(payload.data[0]).color(YELLOW)
                                                .append(" from our store!\n").color(GRAY)
                                                .append("We've applied the purchase to your account, ", FORMATTING)
                                                .append(payload.name).color(GREEN)
                                                .append(".\n\n").color(GRAY)
                                                .append("If you happen to run into any issues regarding\n", FORMATTING)
                                                .append("this please contact us at ", FORMATTING)
                                                .append("neogamesmc.net\n").color(DARK_AQUA)
                                                .append("                                                                           ").strikethrough(true).color(AQUA)
                                                .create());
            }
        }
    }

}
