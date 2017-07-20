package net.neogamesmc.bungee.distribution;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.neogamesmc.bungee.dynamic.ServerCreator;
import net.neogamesmc.bungee.util.Message;

import java.util.Set;
import java.util.function.Consumer;

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
     * Connect a player to the provided server.
     *
     * @param player The player being sent
     * @param info The server to go to
     */
    public void connect(ProxiedPlayer player, ServerInfo info)
    {
        connect(player, info, null);
    }

    /**
     * Connect a player to the provided server.
     *
     * @param player The player being connected
     * @param info The server to connect to
     * @param issueHandler Process issues in sending players via this handler
     */
    public void connect(ProxiedPlayer player, ServerInfo info, Consumer<Throwable> issueHandler)
    {
        if (player != null && info != null)
        {
            player.connect(info, (success, ex) ->
            {
                if (success)
                {
                    player.sendMessage(
                            Message.prefix("Network").append("You're being sent to ").color(ChatColor.GRAY)
                                   .append(info.getName()).color(ChatColor.GREEN).append(".").color(ChatColor.GRAY).create()
                    );
                }
                else if (issueHandler != null)
                {
                    issueHandler.accept(ex);
                }
            });
        }
    }

    /**
     * Sends a player to a server in the provided group.
     *
     * @param player The player
     * @param group The group
     */
    public void sendPlayer(ProxiedPlayer player, String group)
    {
        sendPlayer(player, group, DistributionMethod.LOWEST_FILL_TO_CAPACITY, null);
    }

    /**
     * Sends a player to a server in the provided group via the supplied method.
     *
     * @param player The player
     * @param group The group
     * @param method The method to use
     */
    public void sendPlayer(ProxiedPlayer player, String group, DistributionMethod method, Runnable onFailure)
    {
        if (!currentlySending.contains(player))
        {
            currentlySending.add(player);

            val info = info(group, method);

            if (info != null)
            {
                System.out.println("[Network Director] Sending " + player.getName() + " to " + info.getName() + " via method " + method.name());
                connect(player, info);

                currentlySending.remove(player);
            }
            else if (onFailure != null)
            {
                onFailure.run();
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
