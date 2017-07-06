package net.neogamesmc.core.scoreboard;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.neogamesmc.core.player.Players;
import net.neogamesmc.core.scoreboard.mod.ScoreboardModifier;
import net.neogamesmc.core.scoreboard.title.ScoreboardTitle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/04/2017 (1:15 AM)
 */
@Singleton
public class PlayerSidebarManager implements Listener
{

    /**
     * A common title shared across every scoreboard.
     */
    @Setter @Getter
    private ScoreboardTitle title;

    /**
     * Hold of every sidebar currently tracked.
     */
    private Map<UUID, PlayerSidebar> sidebars = Maps.newHashMap();

    /**
     *
     */
    private Set<ScoreboardModifier> defaultModifiers;

    /**
     * Start tracking a player's sidebar.
     *
     * @param player The player
     * @param bar The bar
     * @return The fresh sidebar
     */
    public PlayerSidebar add(Player player, PlayerSidebar bar)
    {
        sidebars.put(player.getUniqueId(), bar);

        if (title != null)
            bar.objective().setDisplayName(title.current());

        if (!player.getScoreboard().equals(bar.scoreboard()))
            player.setScoreboard(bar.scoreboard());

        if (defaultModifiers != null)
            defaultModifiers.forEach(bar::registerModifier);

        return bar;
    }

    /**
     * Stop tracking a player's sidebar.
     *
     * @param player The player
     * @return This manager
     */
    public PlayerSidebarManager remove(Player player)
    {
        sidebars.remove(player.getUniqueId()).cleanup();
        return this;
    }

    /**
     * Grab a scoreboard instance.
     *
     * @param player The player
     * @return The sidebar
     */
    public PlayerSidebar sidebar(Player player)
    {
        return sidebars.get(player.getUniqueId());
    }

    /**
     *
     * @param modifier
     * @return
     */
    public PlayerSidebarManager addDefaultModifier(ScoreboardModifier modifier)
    {
        if (defaultModifiers == null)
            defaultModifiers = Sets.newHashSet();

        defaultModifiers.add(modifier);
        return this;
    }

    /**
     *
     * @param modifier
     * @return
     */
    public boolean removeDefaultModifier(ScoreboardModifier modifier)
    {
        checkNotNull(defaultModifiers, "Modifiers have yet to be added");

        return defaultModifiers.remove(modifier);
    }

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void handleJoin(PlayerJoinEvent event)
    {
        sidebars.values().forEach(board -> board.activeModifiers().forEach(mod ->
            Players.stream().forEach(player -> mod.playerAdd(player, board.scoreboard()))
        ));
    }

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void handleQuit(PlayerQuitEvent event)
    {
        sidebars.values().forEach(board -> board.activeModifiers().forEach(mod ->
            Players.stream().forEach(player -> mod.playerRemove(player, board.scoreboard()))
        ));
    }

}
