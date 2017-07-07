package net.neogamesmc.core.scoreboard.mod;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/05/2017 (4:07 PM)
 */
public interface ScoreboardModifier
{

    /**
     * Executed when we register this modifier on our manager.
     *
     * @param scoreboard The scoreboard in question
     */
    void start(Scoreboard scoreboard);

    /**
     * Executed when we remove this modifier.
     *
     * @param scoreboard The scoreboard
     */
    void end(Scoreboard scoreboard);

    /**
     * Executed when a player is added to the scoreboard.
     *
     * @param player The player
     * @param scoreboard Scoreboard we're working with
     */
    default void playerAdd(Player player, Scoreboard scoreboard) { }

    /**
     * Executed when a player is removed from the scoreboard.
     *
     * @param player The player
     * @param scoreboard Scoreboard we're working with
     */
    default void playerRemove(Player player, Scoreboard scoreboard) { }

}
