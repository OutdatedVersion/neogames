package net.neogamesmc.bungee.limbo.netty;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.Protocol;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/26/2017 (3:30 PM)
 */
@AllArgsConstructor
public class ChannelInitializer extends io.netty.channel.ChannelInitializer<Channel>
{

    private final UserConnection user;

    @Override
    protected void initChannel(Channel channel) throws Exception
    {
        PipelineUtils.BASE.initChannel(channel);

        channel.pipeline().addAfter(PipelineUtils.FRAME_DECODER, PipelineUtils.PACKET_DECODER, new MinecraftDecoder(Protocol.HANDSHAKE, false, user.getPendingConnection().getVersion()));
        channel.pipeline().addAfter(PipelineUtils.FRAME_PREPENDER, PipelineUtils.PACKET_ENCODER, new MinecraftEncoder(Protocol.HANDSHAKE, false, user.getPendingConnection().getVersion()));

        // channel.pipeline().get(HandlerBoss.class).setHandler();
    }

}
