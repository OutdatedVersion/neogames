package net.neogamesmc.bungee.event;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;
import net.neogamesmc.bungee.dynamic.ServerData;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/08/2017 (2:10 PM)
 */
@AllArgsConstructor
public class AddServerEvent extends Event
{

    /**
     * Information on the just added server.
     */
    public final ServerData data;

}
