package net.neogamesmc.bungee.distribution;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neogamesmc.bungee.dynamic.ServerCreator;

import java.util.Set;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (1:12 AM)
 */
@Singleton
public class PlayerDirector
{

    /**
     * Our server creator
     */
    @Inject private ServerCreator creator;

    /**
     * A list containing every player currently being worked on.
     */
    private Set<ProxiedPlayer> currentlySending = Sets.newConcurrentHashSet();

    /**
     * Sends a player to a server in the provided group.
     *
     * @param player The player
     * @param group The group
     */
    public void sendPlayer(ProxiedPlayer player, String group)
    {
        sendPlayer(player, group, DistributionMethod.FILL_TO_CAPACITY);
    }

    /**
     * Sends a player to a server in the provided group via the supplied method.
     *
     * @param player The player
     * @param group The group
     * @param method The method to use
     */
    public void sendPlayer(ProxiedPlayer player, String group, DistributionMethod method)
    {
        if (!currentlySending.contains(player))
        {
            currentlySending.add(player);

            val info = info(group, method);

            if (info != null)
            {
                System.out.println("[Network Director] Sending " + player.getName() + " to " + info.getName() + " via method " + method.name());
                player.connect(info);

                currentlySending.remove(player);
            }
        }
    }

    /**
     * Grab the {@link ServerInfo} for the provided request.
     *
     * @param group The group
     * @param method Method to distribute by
     * @return The info
     */
    public ServerInfo info(String group, DistributionMethod method)
    {
        return method.get().apply(group, creator);
    }

}
