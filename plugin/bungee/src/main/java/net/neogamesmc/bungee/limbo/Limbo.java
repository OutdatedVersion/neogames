package net.neogamesmc.bungee.limbo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Data;
import lombok.val;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.protocol.packet.Kick;
import net.neogamesmc.bungee.NeoGames;
import net.neogamesmc.bungee.limbo.handle.LimboTask;
import net.neogamesmc.bungee.limbo.netty.LimboDownstream;

import java.util.Map;
import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/24/2017 (4:00 PM)
 */
@Singleton
public class Limbo
{

    /**
     * Whilst processing kick packets we'll check if the message used starts with
     * this character sequence. If it does then we'll send them off to 'limbo'.
     * <p>
     * We keep it lowercase to avoid being case-sensitive.
     *
     * @see LimboDownstream#handle(Kick) Where this is used
     */
    public static final String REQ_SEND_TO_LIMBO = "neogames|send-to-limbo";

    /**
     * The text used to keep the user aware of where they are.
     * <p>
     * This is sent primarily on an <strong>action bar</strong>.
     */
    public static final BaseComponent[] MESSAGE_IN_LIMBO = new ComponentBuilder("You are currently in limbo").color(ChatColor.DARK_AQUA).italic(true).create();

    /**
     * The text used to inform the user of where they are.
     * <p>
     * Sent when first entering limbo (the first and all subsequent times).
     */
    public static final BaseComponent[] MESSAGE_ENTERING_LIMBO = new ComponentBuilder("You are now entering limbo").color(ChatColor.DARK_AQUA).italic(true).create();

    /**
     *
     */
    @Inject private ProxyServer proxy;

    @Inject private NeoGames plugin;

    private Map<UUID, LimboData> data;

    public void sendTo(UserConnection connection)
    {
        val uuid = connection.getUniqueId();

        if (!data.containsKey(uuid))
        {
            data.put(uuid, new LimboData(
                    new LimboTask(plugin, connection, connection.getServer(), false).schedule(),
                    connection,
                    System.currentTimeMillis()
            ));
        }
    }

    public void removeFrom(UserConnection connection)
    {
        val limboData = data.remove(connection.getUniqueId());

        if (limboData != null)
        {
            limboData.task.end();
            connection.sendMessage("Limbo - Send to lobby");
        }
    }

    @Data
    private static class LimboData
    {
        final LimboTask task;
        final UserConnection connection;
        final long enteredAt;
    }

}
