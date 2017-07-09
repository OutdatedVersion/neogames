package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.neogamesmc.common.payload.RemoveFromQueuePayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (4:27 AM)
 */
public class LeaveQueueCommand
{

    /**
     * Local copy of our Redis handler.
     */
    @Inject private RedisHandler redis;

    /**
     * Run the command
     *
     * @param player The player
     */
    @Command ( executor = "leavequeue" )
    public void run(Player player)
    {
        new RemoveFromQueuePayload(player.getUniqueId().toString()).publish(redis);
        Message.prefix("Queue").content("You've requested to be removed from the queue").send(player);
    }

}
