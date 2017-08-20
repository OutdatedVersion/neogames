package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.message.Message;
import net.neogamesmc.core.message.option.format.Color;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (8:08 PM)
 */
public class ServerIDCommand
{

    /**
     * Local copy of our configuration.
     */
    @Inject private ServerConfiguration config;

    @Command ( executor = "id" )
    public void run(Player player)
    {
        Message.prefix("Network").content(config.name, Color.GREEN)
                .content("is assigned to").content("#" + config.id, Color.YELLOW).send(player);
    }

}
