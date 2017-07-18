package net.neogamesmc.core.npc;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_12_R1.Packet;
import net.neogamesmc.common.payload.QueuePlayersForGroupPayload;
import net.neogamesmc.core.text.Colors;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

import static net.md_5.bungee.api.ChatColor.WHITE;
import static net.md_5.bungee.api.ChatColor.YELLOW;


/**
 * Manipulates player packet
 */
public class PacketReader
{

    Player player;
    Channel channel;

    NPCManager manager;

    private long lastClick = System.currentTimeMillis();

    public PacketReader(Player player, NPCManager manager)
    {
        this.manager = manager;
        this.player = player;
    }

    public void inject()
    {
        CraftPlayer cPlayer = (CraftPlayer) this.player;
        channel = cPlayer.getHandle().playerConnection.networkManager.channel;

        channel.pipeline().addAfter("decoder", "neogames-npc-reader", new MessageToMessageDecoder<Packet<?>>()
        {
            @Override
            protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2) throws Exception
            {
                arg2.add(packet);
                processPacket(packet);
            }
        });
    }

    public void uninject()
    {
        if (channel.pipeline().get("neogames-npc-reader") != null)
        {
            channel.pipeline().remove("neogames-npc-reader");
        }
    }

    public void processPacket(Packet<?> packet)
    {
        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity"))
        {
            int id = (Integer) getValue(packet, "a");

            if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT") || getValue(packet, "action").toString().equalsIgnoreCase("ATTACK"))
            {
                if (manager.getNpcs().containsKey(id))
                {
                    if ((System.currentTimeMillis() - lastClick) > 2000)
                    {
                        NPC npc = manager.getNpcs().get(id);

                        switch (npc.type())
                        {
                            case GO_TO_GAME:
                                new QueuePlayersForGroupPayload(npc.data("group"), player.getUniqueId().toString()).publish(manager.redis);
                                break;

                            case TEAM_MEMBER:
                                player.sendMessage(Colors.bold(YELLOW) + "NPC " + YELLOW + npc.getName() + " " + WHITE + npc.data("quote"));
                                break;
                        }

                        lastClick = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    public Object getValue(Object obj, String name)
    {
        try
        {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        }
        catch (Exception e)
        {
            // ignored
        }

        return null;
    }

}