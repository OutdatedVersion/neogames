package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.neogamesmc.common.payload.RequestServerCreationPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.message.Message;
import net.neogamesmc.core.message.option.format.Color;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (5:10 PM)
 */
public class ProvisionServerInGroupCommand
{

    /**
     * Work with our Redis instance
     */
    @Inject private RedisHandler redis;

    @Command ( executor = "provision" )
    @Permission ( Role.ADMIN )
    public void run(Player player,
                    @Necessary ( "Please provide a network group" ) String group)
    {
        new RequestServerCreationPayload(player.getName(), group, "").publish(redis);
        Message.prefix("Network").content("Sent out request to provision server in group:").content(group, Color.GREEN).send(player);
    }

}
