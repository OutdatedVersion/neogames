package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.neogamesmc.common.payload.QueuePlayersForGroupPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.player.Players;
import net.neogamesmc.core.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (8:15 PM)
 */
public class StopServerCommand
{

    /**
     * Local copy of our Redis instance.
     */
    @Inject private RedisHandler redis;

    @Command ( executor = "stop" )
    public void run(Player player)
    {
        new QueuePlayersForGroupPayload("lobby", Players.onlinePlayersUUID()).publish(redis);

        // Two seconds later, shutdown
        Scheduler.delayed(Bukkit::shutdown, 40);
    }

}
