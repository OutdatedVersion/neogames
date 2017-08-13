package net.neogamesmc.core.npc;

import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

import static net.md_5.bungee.api.ChatColor.WHITE;
import static net.md_5.bungee.api.ChatColor.YELLOW;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/11/2017 (1:46 AM)
 */
public enum NPCType
{

    /**
     * This entity represents a member of our team.
     */
    TEAM_MEMBER((player, npc) -> player.sendMessage(
            Message.start().content("NPC").color(YELLOW).bold(true).content(" ")
                           .content(npc.getName()).bold(false).content(" ")
                           .content(npc.data("quote")).color(WHITE)
                           .create()
    )),

    /**
     * This entity is to send you to a game in a specific group.
     */
    GO_TO_GAME();

    /**
     * Default interaction processor.
     */
    public BiConsumer<Player, NPC> defaultHandler;

    /**
     * Constructor (Empty)
     */
    NPCType()
    {
        this.defaultHandler = null;
    }

    /**
     * Constructor
     *
     * @param defaultHandler Default handler used for processing
     */
    NPCType(BiConsumer<Player, NPC> defaultHandler)
    {
        this.defaultHandler = defaultHandler;
    }

}
