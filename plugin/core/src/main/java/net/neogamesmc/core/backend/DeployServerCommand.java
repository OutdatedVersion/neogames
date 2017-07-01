package net.neogamesmc.core.backend;

import com.google.inject.Inject;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.SubCommand;
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
    @Permission ( "network.command.base" )
    public void run(Player player)
    {

    }

    @SubCommand ( of = "deploy", executors = "create" )
    @Permission ( "network.command.create" )
    public void deployServer(Player player)
    {

    }

}
