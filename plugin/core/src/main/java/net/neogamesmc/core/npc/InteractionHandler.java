package net.neogamesmc.core.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.inject.Inject;
import lombok.val;
import org.bukkit.plugin.Plugin;

import static com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction.ATTACK;
import static com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction.INTERACT;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/13/2017 (5:31 PM)
 */
public class InteractionHandler
{

    /**
     *
     */
    @Inject private NPCManager manager;

    @Inject
    public InteractionHandler(Plugin plugin, ProtocolManager protocol)
    {
        protocol.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY)
        {
            @Override
            public void onPacketReceiving(PacketEvent event)
            {
                val action = event.getPacket().getEntityUseActions().read(0);

                if (action == INTERACT || action == ATTACK)
                {
                    int id = event.getPacket().getEntityModifier(event).read(0).getEntityId();
                }
            }

            @Override
            public void onPacketSending(PacketEvent event)
            {

            }
        });
    }

}
