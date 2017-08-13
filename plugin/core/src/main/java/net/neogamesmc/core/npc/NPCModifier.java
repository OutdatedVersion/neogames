package net.neogamesmc.core.npc;

import com.google.inject.Inject;
import lombok.val;
import net.neogamesmc.core.scoreboard.mod.ScoreboardModifier;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/10/2017 (3:42 PM)
 */
public class NPCModifier implements ScoreboardModifier
{

    /**
     * Name of the scoreboard team used.
     */
    private static final String TEAM_NAME = "npcs";

    /**
     * Managing instance.
     */
    @Inject private NPCManager manager;

    /**
     * Start this deal up.
     *
     * @param scoreboard The scoreboard in question
     */
    @Override
    public void start(Scoreboard scoreboard)
    {
        val team = scoreboard.registerNewTeam(TEAM_NAME);

        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }

    /**
     * Cleanup
     *
     * @param scoreboard The scoreboard
     */
    @Override
    public void end(Scoreboard scoreboard)
    {
        scoreboard.getTeam(TEAM_NAME).unregister();
    }

    /**
     * Track changes for a player.
     *
     * @param player The player
     * @param scoreboard Scoreboard we're working with
     */
    @Override
    public void playerAdd(Player player, Scoreboard scoreboard)
    {
        manager.npcs.values().forEach(npc -> scoreboard.getTeam(TEAM_NAME).addEntry(npc.gameProfile().getName()));
    }

}
