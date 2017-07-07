package net.neogamesmc.core.command.messaging;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import org.bukkit.entity.Player;


public class MessageCommand {
    @Inject
    private Database database;

    @Command(executor = {"msg", "pm", "tell", "whisper", "w", "m", "choo"})
    public void run(Player player, @Necessary("Invalid usage! Valid usage: /msg Player (message)") Player target,
                    @Necessary("Invalid usage! Valid usage: /msg Player (message)") String[] message) {

        Role senderRole = database.cacheFetch(player.getUniqueId()).role();
        Role recipientRole = database.cacheFetch(target.getUniqueId()).role();
        BaseComponent[] toComponent = new ComponentBuilder
                ("To ").color(ChatColor.AQUA)
                .append(recipientRole.name.toUpperCase()).color(recipientRole.color)
                .append(" " + target.getName())
                .append(" " + combine(message)).color(ChatColor.WHITE).create();

        BaseComponent[] fromComponent = new ComponentBuilder
                ("From ").color(ChatColor.AQUA)
                .append(senderRole.name.toUpperCase()).color(senderRole.color)
                .append(" " + player.getName())
                .append(" " + combine(message)).color(ChatColor.WHITE).create();

        player.sendMessage(toComponent);
        target.sendMessage(fromComponent);

    }

    private String combine(String[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : array) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString().trim();
    }

}
