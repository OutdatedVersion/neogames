package net.neogamesmc.core.npc;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_11_R1.DataWatcher;
import net.minecraft.server.v1_11_R1.Packet;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;


/**
 * Manipulates player packet
 */
public class PacketReader {

    Player player;
    Channel channel;

    NpcManager manager;

    public PacketReader(Player player, NpcManager manager) {
        this.manager = manager;
        this.player = player;
    }

    public void inject() {
        CraftPlayer cPlayer = (CraftPlayer) this.player;
        channel = cPlayer.getHandle().playerConnection.networkManager.channel;
        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2) throws Exception {
                arg2.add(packet);

                // System.out.println("Read  :: " + player.getName() + " > " + packet.getClass().getSimpleName());
                processPacket(packet);
            }
        });

        channel.pipeline().addLast("PacketInjectorWrite", new MessageToMessageEncoder<Packet<?>>()
        {
            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) throws Exception
            {
                // System.out.println("Write :: " + player.getName() + " > " + packet.getClass().getSimpleName());
                list.add(packet);
                processPacket(packet);
            }
        });

        channel.pipeline().forEach(entry ->
        {
            System.out.println(entry.getKey() + " -> " + entry.getValue().toString());
        });
    }

    public void uninject() {
        if (channel.pipeline().get("PacketInjector") != null) {
            channel.pipeline().remove("PacketInjector");
        }
    }


    public void processPacket(Packet<?> packet) {
        if (packet.getClass().getSimpleName().equals("PacketPlayOutNamedEntitySpawn")) {
            System.out.println("PacketPlayOutNamedEntitySpawn");

            val dataWatcher = ((DataWatcher) getValue(packet, "h"));

            dataWatcher.c().forEach(item -> {
                System.out.println("Data Watcher Value: " + (dataWatcher.get(item.a())));
            });
        }

        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
            int id = (Integer) getValue(packet, "a");


            if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT") || getValue(packet, "action").toString().equalsIgnoreCase("ATTACK")) {
                if (manager.getNpcs().containsKey(id)) {
                    NPC npc = manager.getNpcs().get(id);
                    player.sendMessage(org.bukkit.ChatColor.GOLD + "" + ChatColor.BOLD + "NPC " + org.bukkit.ChatColor.GOLD + " " + npc.getName()  + "  " + ChatColor.WHITE + npc.getQuote());


                }
            }
        }

        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInSettings")) {
            System.out.println("val " + getValue(packet, "e"));
        }

    }

    private String getNPCMessage(String NPCname, String NPCLine) {
        return ChatColor.YELLOW + "[NPC] " + NPCname + ChatColor.WHITE + ": " + NPCLine;


    }


    public void setValue(Object obj, String name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
        }
    }

    public Object getValue(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
        }
        return null;
    }

}