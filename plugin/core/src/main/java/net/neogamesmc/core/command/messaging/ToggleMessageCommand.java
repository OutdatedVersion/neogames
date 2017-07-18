package net.neogamesmc.core.command.messaging;

import com.google.inject.Inject;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/18/2017 (12:47 PM)
 */
public class ToggleMessageCommand
{

    /**
     * Interact with the player's account.
     */
    @Inject private Database database;

    /**
     * Expose a method to toggle the receiving of private messages.
     *
     * @param player The player
     */
    @Command ( executor = "messages" )
    public void run(Player player)
    {
        val account = database.cacheFetch(player.getUniqueId());

        // invert setting
        account.message(!account.messages(), database);

        Message.prefix("Messaging").content("You now have messaging " + (account.messages() ? "enabled" : "disabled"), account.messages() ? ChatColor.GREEN : ChatColor.RED).send(player);
    }

}