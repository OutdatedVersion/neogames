package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.text.Message;
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
        Message.prefix("Network").content(config.name, ChatColor.GREEN)
                .content("is assigned to").content("#" + config.id, ChatColor.YELLOW).send(player);
    }

}
