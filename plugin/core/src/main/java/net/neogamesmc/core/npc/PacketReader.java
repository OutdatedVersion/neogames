package net.neogamesmc.core.npc;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.md_5.bungee.api.ChatColor;
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

    NPCManager manager;

    public PacketReader(Player player, NPCManager manager) {
        this.manager = manager;
        this.player = player;
    }

    public void inject() {
        CraftPlayer cPlayer = (CraftPlayer) this.player;
        channel = cPlayer.getHandle().playerConnection.networkManager.channel;
        channel.pipeline().addAfter("decoder", "neogames_npc_injector", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2) throws Exception {
                arg2.add(packet);
                processPacket(packet);
            }
        });
    }

    public void uninject() {
        if (channel.pipeline().get("neogames_npc_injector") != null) {
            channel.pipeline().remove("neogames_npc_injector");
        }
    }


    public void processPacket(Packet<?> packet) {
        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
            int id = (Integer) getValue(packet, "a");


            if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT") || getValue(packet, "action").toString().equalsIgnoreCase("ATTACK")) {
                if (manager.getNpcs().containsKey(id)) {
                    NPC npc = manager.getNpcs().get(id);
                    player.sendMessage(org.bukkit.ChatColor.GOLD + "" + ChatColor.BOLD + "NPC " + org.bukkit.ChatColor.GOLD + " " + npc.getName()  + "  " + ChatColor.WHITE + npc.getQuote());
                }
            }
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