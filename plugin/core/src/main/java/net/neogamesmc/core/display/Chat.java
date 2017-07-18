package net.neogamesmc.core.display;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.val;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.operation.RawFetchOperation;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.payload.PunishmentPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.common.time.TimeFormatting;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.player.Players;
import net.neogamesmc.core.text.Message;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
     * SQL query to grab active mutes on a player's account.
     */
    private static final String SQL_FETCH_MUTES = "SELECT id,reason,expires_at FROM punishments WHERE target=? AND revoked != 1 AND type='MUTE' AND (expires_at IS NULL OR expires_at > NOW()) ORDER BY expires_at DESC LIMIT 1;";

    /**
     * In memory cache of mute data for a player.
     */
    private final Cache<UUID, MuteData> CACHE_MUTES = CacheBuilder.newBuilder().build();

    /**
     * A collection of players who we've recently informed of their mute.
     */
    private final Cache<UUID, Boolean> INFORMED = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).build();

    /**
     * Whether or not chat is currently disabled.
     */
    private volatile boolean isSilenced;

    /**
     * Local copy of our database.
     */
    @Inject private Database database;

    @Inject
    public Chat(RedisHandler redis)
    {
        redis.registerHook(this);
    }

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
        val uuid = event.getPlayer().getUniqueId();

        val data = CACHE_MUTES.getIfPresent(uuid);

        // They're muted
        if (data != null)
        {
            if (INFORMED.getIfPresent(uuid) == null)
            {
                event.getPlayer().sendMessage(new ComponentBuilder("Woah, you're currently muted!").color(RED).bold(true)
                        .append("\nWhich will remain active until ").color(GRAY)
                        .append(data.expiresAt).color(YELLOW)
                        .append("\nReason: ").color(GRAY)
                        .append(data.reason).color(WHITE).create());

                INFORMED.put(uuid, true);
            }

            return;
        }


        val role = database.cacheFetch(event.getPlayer().getUniqueId()).role();

        if (isSilenced && !role.compare(Role.ADMIN))
        {
            SILENCE_INFORM.sendAsIs(event.getPlayer());
            return;
        }

        val name = event.getPlayer().getName();

        val builder = Message.start();

        if (role != Role.PLAYER)
        {
            // start with the player's display role
            builder.content(role.name.toUpperCase() + " ", role.color, String::trim).bold(true);
        }


        // username -- gray w/o role, green if present
        builder.content(name, role == Role.PLAYER ? GRAY : GREEN, role == Role.PLAYER ? String::trim : null);


        // Add the message content to the final message
        for (String word : event.getMessage().split(" "))
            builder.content(word, WHITE, Text::stripProtocol);


        // Send out the message
        val message = builder.create();
        Players.stream().forEach(player -> player.sendMessage(message));

        // Log -- printf() gets super weird?
        System.out.println(format("[Chat] %s %s: %s", role.name(), name, event.getMessage()));
    }

    // temp until mongo
    @EventHandler
    public void fetchMutes(AsyncPlayerPreLoginEvent event)
    {
        try
        {
            new RawFetchOperation(SQL_FETCH_MUTES).data(event.getUniqueId()).task(set ->
            {
                if (set.next())
                {
                    CACHE_MUTES.put(event.getUniqueId(), new MuteData(set.getString("reason"), TimeFormatting.format(set.getTimestamp("expires_at").toInstant())));
                }
            }).sync(database);
        }
        catch (Exception ex)
        {
            Issues.handle("Fetch Mute Data", ex);
        }
    }

    @EventHandler
    public void invalidateCache(PlayerQuitEvent event)
    {
        CACHE_MUTES.invalidate(event.getPlayer().getUniqueId());
        INFORMED.invalidate(event.getPlayer().getUniqueId());
    }

    /**
     * Represents a mute on a player's account.
     */
    @EqualsAndHashCode
    @AllArgsConstructor
    private static class MuteData
    {
        String reason;
        String expiresAt;
    }

    @HandlesType ( PunishmentPayload.class )
    public void process(PunishmentPayload payload)
    {
        if (payload.type.equals("MUTE"))
        {
            val target = Bukkit.getPlayer(payload.targetName);

            if (target != null)
                CACHE_MUTES.put(target.getUniqueId(), new MuteData(payload.reason, TimeFormatting.format(Instant.ofEpochMilli(payload.expiresAt))));
        }
    }

}
