package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.display.Chat;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (12:27 AM)
 */
public class SilenceChatCommand
{

    /**
     * The only managing chat instance.
     */
    @Inject private Chat chat;

    @Command ( executor = "silence" )
    @Permission ( Role.ADMIN )
    public void execute(Player player)
    {
        chat.toggleChat();
    }

}
