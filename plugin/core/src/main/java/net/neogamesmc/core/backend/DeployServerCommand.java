package net.neogamesmc.core.backend;

import com.google.inject.Inject;
import net.neogamesmc.common.backend.RequestServerCreationPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (5:10 PM)
 */
public class DeployServerCommand
{

    // /deploy new game chunk_runner

    /**
     * Work with our Redis instance
     */
    @Inject private RedisHandler redis;

    @Command ( executor = "deploy" )
    @Permission ( "network.command.create" )
    public void run(Player player, @Necessary("You missed the type") String type, @Necessary("Please provide a network group") String group)
    {
        new RequestServerCreationPayload(player.getName(), type, group).publish(redis);
    }

}
