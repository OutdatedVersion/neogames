package net.neogamesmc.core.payment;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.payload.TransactionNoticePayload;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.reference.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        final Player target = Bukkit.getPlayerExact(payload.name);

        if (target != null)
        {
            if (payload.type.equals("ROLE"))
            {
                database.cacheFetch(target.getUniqueId()).role = Role.valueOf(payload.data[0]);

                target.sendMessage(new ComponentBuilder("                                                                           ").strikethrough(true)
                                                .append("\nPurchase Received!\n\n").color(DARK_AQUA).bold(true)
                                                .append("Thanks for grabbing ", NONE).color(GRAY)
                                                .append(payload.data[0]).color(YELLOW)
                                                .append(" from our store!\n").color(GRAY)
                                                .append("We've applied the purchase to your account, ", FORMATTING)
                                                .append(payload.name).color(GREEN)
                                                .append(".\n\n").color(GRAY)
                                                .append("If you happen to run into any issues regarding this", FORMATTING)
                                                .append("please contact us neogamesmc.net", FORMATTING)
                                                .append("                                                                           ").strikethrough(true).create());
            }
        }
    }

}