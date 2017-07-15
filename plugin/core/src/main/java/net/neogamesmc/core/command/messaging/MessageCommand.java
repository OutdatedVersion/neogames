package net.neogamesmc.core.command.messaging;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.text.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Shahar (Nokoa)
 * @since Jul/7/2017
 */
public class MessageCommand
{

    @Inject private Database database;
    private HashMap<UUID, UUID> replies = new HashMap<>();

    @Command ( executor = { "msg", "pm", "tell", "whisper", "w", "m", "choo" } )
    public void run(Player player,
                    @Necessary ( "Invalid usage! Valid usage: /msg Player (message)" ) String target,
                    @Necessary ( "Invalid usage! Valid usage: /msg Player (message)" ) String[] message)
    {
        Player targetPlayer = null;
        for (Player all : Bukkit.getOnlinePlayers())
        {
            if (all.getName().equalsIgnoreCase(target))
            {
                targetPlayer = all;
            }
        }

        if (targetPlayer == null)
        {
            player.sendMessage(Message.prefix("Messaging").content("Player not found.", ChatColor.RED).create());
            return;
        }

        replies.put(player.getUniqueId(), targetPlayer.getUniqueId());
        sendMessage(player, message, targetPlayer);


    }

    @Command ( executor = { "reply", "r" } )
    public void run(Player player, @Necessary ( "Invalid usage! Valid usage: /r (message)" ) String[] message)
    {

        if (replies.containsKey(player.getUniqueId()))
        {
            Player targetPlayer = Bukkit.getPlayer(replies.get(player.getUniqueId()));

            if (targetPlayer == null)
            {
                player.sendMessage(Message.prefix("Messaging").content("Player is no longer online.", ChatColor.RED).create());
                return;
            }

            sendMessage(player, message, targetPlayer);
        }
        else
        {
            player.sendMessage(Message.prefix("Messaging").content("You have not messaged anyone recently.", ChatColor.RED).create());

        }

    }

    private void sendMessage(Player player, String[] message, Player target)
    {
        Role senderRole = database.cacheFetch(player.getUniqueId()).role();
        Role recipientRole = database.cacheFetch(target.getUniqueId()).role();
        BaseComponent[] toComponent = new ComponentBuilder("To ").color(ChatColor.AQUA).append(recipientRole.name.toUpperCase()).color(recipientRole.color).append(" " + target.getName()).append(" " + Text.convertArray(message)).color(ChatColor.AQUA).create();

        BaseComponent[] fromComponent = new ComponentBuilder("From ").color(ChatColor.AQUA).append(senderRole.name.toUpperCase()).color(senderRole.color).append(" " + player.getName()).append(" " + Text.convertArray(message)).color(ChatColor.AQUA).create();

        player.sendMessage(toComponent);
        target.sendMessage(fromComponent);
    }


}
