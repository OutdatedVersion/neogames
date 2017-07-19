package net.neogamesmc.core.npc;

import net.minecraft.server.v1_11_R1.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

class Reflections
{

    public void setValue(Object obj, String name, Object value)
    {
        try
        {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e)
        {
        }
    }

    public Object getValue(Object obj, String name)
    {
        try
        {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e)
        {
        }
        return null;
    }

    public void sendPacket(Packet<?> packet, Player player)
    {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendPacket(Packet<?> packet)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            sendPacket(packet, player);
        }
    }

}
