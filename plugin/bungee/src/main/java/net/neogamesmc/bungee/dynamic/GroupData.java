package net.neogamesmc.bungee.dynamic;

import com.google.common.collect.Sets;
import lombok.Data;
import net.neogamesmc.common.number.NumberProvider;

import java.util.Set;

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
    public NumberProvider idProvider = new NumberProvider(1);

    /**
     * The servers within this group.
     */
    public Set<ServerData> servers = Sets.newConcurrentHashSet();

}
