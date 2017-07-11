package net.neogamesmc.core.display;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.text.Message;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static java.lang.String.format;
import static net.md_5.bungee.api.ChatColor.*;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/18/2017 (3:34 AM)
 */
@Singleton
@ParallelStartup
public class Chat implements Listener
{

    /**
     * The message used to let players know that chat is currently disabled when they attempt to send a message.
     */
    private static final Message SILENCE_INFORM = Message.start().content("Chat is currently disabled.", RED).bold(true).italic(true);

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
     */
    public void toggleChat()
    {
        this.isSilenced = !isSilenced;
        Message.start().content("Public chat has been " + (isSilenced ? "disabled." : "enabled."), RED).italic(true).bold(true).sendAsIs();
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

        val builder = Message.start()
                            // start with the player's display role
                            .content(role == Role.PLAYER ? "" : role.name.toUpperCase() + " ", role.color, String::trim).bold(true)
                            // username -- gray w/o role, green if present
                            .content(name).color(role == Role.PLAYER ? GRAY : GREEN);


        // Add the message content to the final message
        for (String word : event.getMessage().split(" "))
            builder.content(" " + word, WHITE, Text::stripProtocol);


        // Send out the message
        val message = builder.create();
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));

        // Log -- printf() gets super weird?
        System.out.println(format("[Chat] %s %s: %s", role.name(), name, event.getMessage()));
    }

}
