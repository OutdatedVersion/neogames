package net.neogamesmc.core.event;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.neogamesmc.common.reference.Role;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (7:00 PM)
 */
@RequiredArgsConstructor
@EqualsAndHashCode ( callSuper = false )
public class UpdatePlayerRoleEvent extends Event
{

    /**
     * Bukkit Handlers
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * The player being updated.
     */
    public final Player player;

    /**
     * The role the player now has.
     */
    public final Role fresh;

    /**
     * The role the player had before this update.
     */
    public final Role previous;

    /**
     * Bukkit
     * @return Handler List
     */
    @Override
    public HandlerList getHandlers()
    {
        return HANDLER_LIST;
    }

    /**
     * Bukkit
     * @return Handler List
     */
    public static HandlerList getHandlerList()
    {
        return HANDLER_LIST;
    }

}
