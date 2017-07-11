package net.neogamesmc.core.command;

import net.md_5.bungee.api.ChatColor;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/10/2017 (10:05 PM)
 */
public class LinkCommands
{

    /**
     * Hook a player up with our Discord guild invite.
     *
     * @param player The player
     */
    @Command ( executor = { "discord", "dc" } )
    public void discord(Player player)
    {
        message(player, "discord.neogamesmc.net");
    }

    /**
     * Inform the provided player of our main site.
     *
     * @param player The player
     */
    @Command ( executor = { "website", "forums", "forum", "form", "forms" } )
    public void website(Player player)
    {
        message(player, "neogamesmc.net");
    }

    /**
     * Send a message highlighting the provided link.
     *
     * @param player The player having the message sent to them
     * @param address The site address
     */
    private static void message(Player player, String address)
    {
        Message.start().content("Join us at", ChatColor.GRAY).bold(true)
                       .content("https://" + address, ChatColor.GREEN, Text::stripProtocol).bold(true)
                       .send(player);
    }

}
