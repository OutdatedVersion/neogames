package net.neogamesmc.bungee.limbo.netty;

import lombok.ToString;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.protocol.packet.Kick;
import net.neogamesmc.bungee.limbo.Limbo;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/24/2017 (4:11 PM)
 */
@ToString
public class LimboDownstream extends DownstreamBridge
{

    private final ProxyServer bungee;

    private final UserConnection connection;

    private final ServerConnection serverConnection;

    public LimboDownstream(ProxyServer bungee, UserConnection connection, ServerConnection connectionServer)
    {
        super(bungee, connection, connectionServer);

        this.bungee = bungee;
        this.connection = connection;
        this.serverConnection = connectionServer;
    }

    @Override
    public void exception(Throwable throwable) throws Exception
    {
        // When something goes wrong with the server the player is connected to
        // this will be executed.
        //
        // Instead of sending them to the proxy's fallback server or disconnecting them
        // we will attempt to handle this on our own.


        // the player has already been connected elsewhere
        if (serverConnection.isObsolete())
            return;

        // circumvent the player's disconnection from the proxy (DownstreamBridge#disconnected)
        serverConnection.setObsolete(true);
    }

    /**
     * This is called when the client is being sent a kick packet.
     *
     * @param kick The packet wrapper
     * @throws Exception In the event that something goes wrong
     */
    @Override
    public void handle(Kick kick) throws Exception
    {
        if (kick.getMessage().toLowerCase().startsWith(Limbo.REQ_SEND_TO_LIMBO))
        {


            throw CancelSendSignal.INSTANCE;
        }
        else
            super.handle(kick);
    }

}
