package net.neogamesmc.bungee.limbo;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.protocol.packet.Kick;
import net.neogamesmc.bungee.NeoGames;
import net.neogamesmc.bungee.limbo.netty.LimboDownstream;

import java.util.List;
import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/24/2017 (4:00 PM)
 */
@Singleton
public class Limbo implements Listener
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
     *
     */
    @Inject private ProxyServer proxy;

    /**
     *
     */
    private List<UUID> present = Lists.newCopyOnWriteArrayList();

    @Inject
    public Limbo(NeoGames plugin)
    {

    }

    public void sendTo(UserConnection connection)
    {

    }

    public void removeFrom()
    {

    }

}
