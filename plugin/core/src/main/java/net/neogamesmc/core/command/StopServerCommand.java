package net.neogamesmc.core.command;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import net.neogamesmc.common.payload.QueuePlayersForGroupPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.player.Players;
import net.neogamesmc.core.scheduler.Scheduler;
import net.neogamesmc.core.text.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (8:15 PM)
 */
public class StopServerCommand
{

    /**
     * List used to ensure someone really wants to stop a server by having them execute stop twice.
     */
    private List<UUID> executed = Lists.newArrayList();
    /**
     * Local copy of our Redis instance.
     */
    @Inject private RedisHandler redis;

    @Command ( executor = "stop" )
    @Permission ( Role.ADMIN )
    public void run(Player player)
    {

        if(!executed.contains(player.getUniqueId())) {
            Message.prefix("[Server Stop]").content("Are you sure you want to stop this server? Execute /stop again to confirm").send(player);
            executed.add(player.getUniqueId());
            return;
        }
        new QueuePlayersForGroupPayload("lobby", Players.onlinePlayersUUID()).publish(redis);

        // Two seconds later, shutdown
        Scheduler.delayed(Bukkit::shutdown, 40);
    }

}
