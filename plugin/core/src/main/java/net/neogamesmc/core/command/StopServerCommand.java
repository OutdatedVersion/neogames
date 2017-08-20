package net.neogamesmc.core.command;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import lombok.val;
import net.neogamesmc.common.payload.QueuePlayersForGroupPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.message.Message;
import net.neogamesmc.core.message.option.event.Click;
import net.neogamesmc.core.message.option.format.Color;
import net.neogamesmc.core.message.option.format.Style;
import net.neogamesmc.core.player.Players;
import net.neogamesmc.core.scheduler.Scheduler;
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
        val uuid = player.getUniqueId();

        if (!executed.contains(uuid))
        {
            Message.start().content("Click here to verify you want to close this server.", Color.RED, Style.BOLD, Click.command("/stop")).sendAsIs(player);

            executed.add(uuid);

            // If the player doesn't repeat the command within 10 seconds, make them type it twice again
            Scheduler.delayed(() -> executed.remove(uuid), 200);

            return;
        }

        new QueuePlayersForGroupPayload("lobby", Players.onlinePlayersUUID()).publish(redis);

        // Two seconds later, shutdown
        Scheduler.delayed(Bukkit::shutdown, 40);
    }

}
