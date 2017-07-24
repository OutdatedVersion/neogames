package net.neogamesmc.core.command.messaging;

import com.google.inject.Inject;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
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
                    @Necessary ( "Try using /m player (message)" ) String target,
                    @Necessary ( "You didn't provide any message to send!" ) String[] message)
    {
        Player targetPlayer = Bukkit.getPlayer(target);

        if (targetPlayer == null)
        {
            Message.prefix("Messaging").content("We could not find any player by that name", ChatColor.RED).send(player);
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
                Message.prefix("Messaging").content("That player is no longer online", ChatColor.RED).send(player);
                return;
            }

            sendMessage(player, message, targetPlayer);
        }
        else
        {
            Message.prefix("Messaging").content("You have not messaged anyone recently", ChatColor.RED).send(player);
        }
    }

    private void sendMessage(Player player, String[] message, Player target)
    {
        Role senderRole = database.cacheFetch(player.getUniqueId()).role();

        val recipient = database.cacheFetch(target.getUniqueId());

        if (!recipient.messages())
        {
            Message.prefix("Messaging").player(target).content("may not receive messages at the moment!").sendAsIs(player);
            return;
        }

        val recipientRole = recipient.role();

        ComponentBuilder toComponent = new ComponentBuilder("To ").color(ChatColor.AQUA);
        appendRole(toComponent, recipientRole);

        ComponentBuilder fromComponent = new ComponentBuilder("From ").color(ChatColor.AQUA);
        appendRole(fromComponent, senderRole);

        player.sendMessage(toComponent.append(" " + target.getName()).append(" " + Text.convertArray(message)).color(ChatColor.AQUA).create());
        target.sendMessage(fromComponent.append(" " + player.getName()).append(" " + Text.convertArray(message)).color(ChatColor.AQUA).create());
    }

    private static void appendRole(ComponentBuilder builder, Role role)
    {
        if (role != Role.PLAYER)
        {
            builder.append(role.name.toUpperCase()).color(role.color);
        }
        else builder.color(ChatColor.GRAY);
    }

}
