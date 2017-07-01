package net.neogamesmc.network.data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (7:13 AM)
 */
public class GroupData
{

    /**
     * The total count of servers in this group.
     */
    public AtomicInteger serverCount = new AtomicInteger();

}
