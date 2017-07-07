package net.neogamesmc.core.backend;

import com.google.inject.Inject;
import net.neogamesmc.common.payload.RequestServerCreationPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
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

    // /deploy game chunk_runner

    /**
     * Work with our Redis instance
     */
    @Inject private RedisHandler redis;

    @Command ( executor = "deploy" )
    @Permission ( Role.ADMIN )
    public void run(Player player,
                    @Necessary ( "Please provide a network group" ) String group,
                    @Necessary ( "Be sure to provide a server type" ) String type)
    {
        new RequestServerCreationPayload(player.getName(), group, type).publish(redis);
    }

}
