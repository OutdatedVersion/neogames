package net.neogamesmc.bungee.distribution;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neogamesmc.bungee.connection.DataHandler;

import java.util.List;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (1:12 AM)
 */
@Singleton
public class PlayerDirector
{

    /**
     * Work with server data.
     */
    @Inject private DataHandler data;

    private List<ProxiedPlayer> currentlySending = Lists.newCopyOnWriteArrayList();

    public void sendPlayer(ProxiedPlayer player, String group)
    {
        sendPlayer(player, group, data.methodFor(group));
    }

    /**
     *
     * @param player
     * @param group
     * @param method
     */
    public void sendPlayer(ProxiedPlayer player, String group, DistributionMethod method)
    {
        if (!currentlySending.contains(player))
        {
            currentlySending.add(player);

            val info = method.get().apply(group, data);

            if (info != null)
            {
                System.out.println("[Network Director] Sending " + player.getName() + " to " + info.getName() + " via method " + method.name());
                player.connect(info);

                currentlySending.remove(player);
            }
        }
    }

}
