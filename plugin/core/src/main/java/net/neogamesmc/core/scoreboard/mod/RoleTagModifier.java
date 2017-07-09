package net.neogamesmc.core.scoreboard.mod;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.reference.Role;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/05/2017 (4:00 PM)
 */
public class RoleTagModifier implements ScoreboardModifier
{

    /**
     * Interact with player data.
     */
    @Inject private Database database;

    /**
     * Map every {@link Role} to its internal ID for this instance.
     */
    private Map<Role, String> idToRole = Maps.newHashMap();

    /**
     * Populate teams for our roles.
     *
     * @param scoreboard Scoreboard being worked with
     */
    @Override
    public void start(Scoreboard scoreboard)
    {
        for (Role role : Role.VALUES)
        {
            val id = idToRole.computeIfAbsent(role, ignore -> UUID.randomUUID().toString().substring(0, 8));

            // prefix
            if (role == Role.PLAYER)
                scoreboard.registerNewTeam(id).setPrefix(ChatColor.RESET + "" + ChatColor.GRAY);
            else
                scoreboard.registerNewTeam(id).setPrefix(role.toName() + ChatColor.RESET + " " + ChatColor.GREEN);
        }
    }

    /**
     * Cleanup teams
     *
     * @param scoreboard The scoreboard being worked with
     */
    @Override
    public void end(Scoreboard scoreboard)
    {
        idToRole.values().stream().map(scoreboard::getTeam).forEach(Team::unregister);
    }

    /**
     * Apply the player's tag.
     *
     * @param player The player
     * @param scoreboard The scoreboard we're working with
     */
    @Override
    public void playerAdd(Player player, Scoreboard scoreboard)
    {
        team(player, scoreboard).addEntry(player.getName());
    }

    /**
     * Cleanup when the player leaves.
     *
     * @param player The player
     * @param scoreboard The scoreboard we're working with
     */
    @Override
    public void playerRemove(Player player, Scoreboard scoreboard)
    {
        // team(player, scoreboard).removeEntry(player.getName());
    }

    /**
     * Grab a {@link Team} for the role the provided player has.
     *
     * @param player The player in question
     * @param scoreboard Scoreboard being used
     * @return The team
     */
    private Team team(Player player, Scoreboard scoreboard)
    {
        return scoreboard.getTeam(idToRole.get(database.cacheFetch(player.getUniqueId()).role()));
    }
}
