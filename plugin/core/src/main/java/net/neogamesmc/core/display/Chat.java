package net.neogamesmc.core.display;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.text.Message;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static java.lang.String.format;
import static net.md_5.bungee.api.ChatColor.*;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/18/2017 (3:34 AM)
 */
@Singleton
@ParallelStartup
public class Chat implements Listener
{

    /**
     * The message used to let players know that
     * chat is currently disabled when they attempt
     * to send a message.
     */
    private static final Message SILENCE_INFORM = Message.start().content("Chat is currently disabled.", RED).bold().italic();

    /**
     * Whether or not chat is currently disabled.
     */
    private volatile boolean isSilenced;

    /**
     * Local copy of our database.
     */
    @Inject private Database database;

    /**
     * Toggle the usability state of chat.
     *
     * @return This instance, for chaining.
     */
    public Chat toggleChat()
    {
        this.isSilenced = !isSilenced;
        Message.start().content("Public chat has been " + (isSilenced ? "disabled." : "enabled."), RED).italic().bold().sendAsIs();

        return this;
    }

    @EventHandler
    public void handleChat(AsyncPlayerChatEvent event)
    {
        event.setCancelled(true);

        val role = database.cacheFetch(event.getPlayer().getUniqueId()).role();

        if (isSilenced && !role.compare(Role.ADMIN))
        {
            SILENCE_INFORM.sendAsIs(event.getPlayer());
            return;
        }

        val name = event.getPlayer().getName();

        val message = new ComponentBuilder
                            // start with the player's display role
                            (role == Role.DEFAULT ? "" : role.name.toUpperCase() + " ").color(role.color).bold(true)
                            // username -- gray w/o role, green if present
                            .append(name, NONE).color(role == Role.DEFAULT ? GRAY : GREEN)
                            // the message
                            .append(" " + event.getMessage()).color(WHITE).create();


        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
        System.out.println(format("[Chat] %s %s: %s", role.name(), name, event.getMessage()));
        // printf() gets super weird?
    }

}
