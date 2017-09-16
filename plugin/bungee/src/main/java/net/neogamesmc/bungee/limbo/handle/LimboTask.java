package net.neogamesmc.bungee.limbo.handle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import lombok.AllArgsConstructor;
import lombok.val;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.neogamesmc.bungee.NeoGames;
import net.neogamesmc.bungee.limbo.Limbo;
import net.neogamesmc.bungee.limbo.netty.ChannelInitializer;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/27/2017 (12:21 PM)
 */
@AllArgsConstructor
public class LimboTask implements Runnable
{

    /**
     *
     */
    private static final int ALLOWANCE = 5000;

    private final NeoGames plugin;

    private final UserConnection connection;

    private final ServerConnection serverConnection;

    private volatile boolean running;

    public LimboTask schedule()
    {
        running = true;
        return this;
    }

    public void end()
    {
        running = false;
    }

    /**
     *
     */
    @Override
    public void run()
    {
        // Send out the action bar letting them know where they are
        connection.sendMessage(ChatMessageType.ACTION_BAR, Limbo.MESSAGE_IN_LIMBO);

        // Open up a connection
        val initializer = new ChannelInitializer(connection);

        ChannelFutureListener listener = future ->
        {
            if (future.isSuccess())
            {
                // Close out this connection (will reopen on next cycle)
                future.channel().close();

                // Keep the client from timing out
                // We have a 20 second (maximum) allowance for sending this packet
                // http://wiki.vg/Protocol#Keep_Alive_.28clientbound.29
                connection.unsafe().sendPacket(new KeepAlive(ThreadLocalRandom.current().nextInt()));

                // keep their connection live by repeating this process
                if (running)
                    plugin.asyncDelayed(this, ALLOWANCE, TimeUnit.MILLISECONDS);
            }
            else
            {
                System.out.println("NOPE");
                System.out.println("NOPE");
                System.out.println("NOPE");
                System.out.println("NOPE");
                System.out.println("NOPE");
                System.out.println("NOPE");
                System.out.println("NOPE");
                System.out.println("NOPE");
            }
        };


        val bootstrap = new Bootstrap()
                                .channel(PipelineUtils.getChannel())
                                .group(serverConnection.getCh().getHandle().eventLoop())
                                .handler(initializer)
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ALLOWANCE)
                                .remoteAddress(serverConnection.getAddress());

        bootstrap.connect().addListener(listener);
    }

}
