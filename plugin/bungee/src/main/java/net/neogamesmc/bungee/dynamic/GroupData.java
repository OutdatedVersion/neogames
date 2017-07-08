package net.neogamesmc.bungee.dynamic;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (6:22 PM)
 */
@Data
public class GroupData
{

    /**
     * The amount of servers in this group.
     */
    public AtomicInteger serverCount = new AtomicInteger(0);

    /**
     * The servers within this group.
     */
    public Set<ServerData> servers = Sets.newConcurrentHashSet();

}
