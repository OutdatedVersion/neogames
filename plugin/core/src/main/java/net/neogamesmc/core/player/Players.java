package net.neogamesmc.core.player;

import net.neogamesmc.core.text.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.md_5.bungee.api.ChatColor.YELLOW;
import static net.neogamesmc.core.text.Message.start;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (7:41 PM)
 */
public class Players
{

    /**
     * Grabs a {@link Stream} of every online player.
     *
     * @return The stream of players
     */
    public static Stream<? extends Player> stream()
    {
        return Bukkit.getOnlinePlayers().stream();
    }

    /**
     * Looks for a player matching the
     * name provided on the current server
     *
     * @param host   person looking for said player
     * @param target the player
     * @param inform whether or not to send updates regarding the status of the search
     *
     * @return the player or null
     */
    public static Player find(Player host, String target, boolean inform)
    {
        inform = inform && host != null;

        if (target.length() > 16)
        {
            if (inform)
                start().content(target, YELLOW).content("is too long! (>16 characters)").sendAsIs(host);

            return null;
        }

        for (char character : target.toCharArray())
        {
            if (!Character.isLetterOrDigit(character) && character != '_')
            {
                if (inform)
                    start().content(target, YELLOW).content("is not a valid name!").sendAsIs(host);

                return null;
            }
        }

        if (target.equalsIgnoreCase("me"))
            return host;


        final List<Player> matches = stream().filter(player -> player.getName().toLowerCase().contains(target.toLowerCase()))
                                              .collect(Collectors.toList());

        if (matches.size() != 1)
        {
            if (inform)
            {
                if (matches.size() != 0)
                    start().content("Matches for ").content(target, YELLOW).content(" (").content(String.valueOf(matches.size()), GREEN).content(")").sendAsIs(host);

                if (matches.size() > 0)
                {
                    final Message message = start().content("Matched names: ");

                    matches.forEach(match -> message.content(match.getName(), YELLOW).content(", "));
                    message.sendAsIs(host);
                }
            }

            return null;
        }

        return matches.get(0);
    }

}
