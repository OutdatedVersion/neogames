package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.neogamesmc.common.payload.RequestProxyActionPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.neogamesmc.common.payload.RequestProxyActionPayload.Action.PURGE_PUNISHMENT_CACHE;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (2:09 AM)
 */
public class PurgeProxyPunishmentCacheCommand
{

    /**
     * Local instance of our Redis wrapper.
     */
    @Inject private RedisHandler redis;

    @Command ( executor = "purgeproxycache" )
    @Permission ( Role.DEV )
    public void execute(Player player)
    {
        new RequestProxyActionPayload(PURGE_PUNISHMENT_CACHE).publish(redis);
        Message.prefix("Network").content("Propagating request to purge cache", GREEN).send(player);
    }

}
