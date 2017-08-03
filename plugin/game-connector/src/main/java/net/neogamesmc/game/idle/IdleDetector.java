package net.neogamesmc.game.idle;

import com.google.common.collect.Maps;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/02/2017 (10:51 PM)
 */
public class IdleDetector implements Listener
{

    // TODO(Ben): replace with fastutil primitive map

    private Map<UUID, Long> lastActivity = Maps.newHashMap();



}
