package net.neogamesmc.core.display;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.event.UpdatePlayerRoleEvent;
import net.neogamesmc.core.player.Players;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.function.Function;

import static net.md_5.bungee.api.ChatColor.*;
import static net.neogamesmc.common.reference.Role.PLAYER;

/**
 * Adds formatting to the player-list.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/19/2017 (2:41 AM)
 */
@Singleton
@ParallelStartup
public class PlayerList implements Listener
{

    /**
     * The custom header/footer for our player list.
     */
    private static final BaseComponent[] LINE_TOP = new ComponentBuilder("Neo").bold(true).color(YELLOW).append("Games\n").color(GOLD)
                                                                 .append("Address - ").bold(false).color(GREEN).append("play.neogamesmc.net").color(YELLOW)
                                                                 .append(" ❚ ").color(RED)
                                                                 .append("Site - ").color(GREEN).append("neogamesmc.net").color(GOLD)
                                                                 .create(),

                                      // bottom half
                                      LINE_BOTTOM = new ComponentBuilder("Purchase").color(YELLOW).append(" ranks ").color(AQUA).append("to help keep us running & get some unique perks!").color(YELLOW)
                                                                 .append("\nThanks for the support!").color(GRAY).append(" :)").color(DARK_GRAY).create();

    /**
     * How to display the player's role in our list.
     */
    private static final Function<Role, String> LIST_FORMAT = role -> role.color + "" + ChatColor.BOLD + role.name.toUpperCase() + " ";

    /**
     * Local database instance.
     */
    @Inject private Database database;

    /**
     * The current mode this
     */
    @Getter
    private Mode mode = Mode.ROLES;

    /**
     * Apply the display mode when a player logs in.
     *
     * @param event The event
     */
    @EventHandler
    public void apply(PlayerLoginEvent event)
    {
        playerList(event.getPlayer());
    }

    /**
     * Set the header/footer on the player list.
     *
     * @param event The event
     */
    @EventHandler
    public void applyHeaderFooter(PlayerJoinEvent event)
    {
        event.getPlayer().setPlayerListHeaderFooter(LINE_TOP, LINE_BOTTOM);
    }

    /**
     * When a player's role is updated somewhere, update it instantly.
     *
     * @param event The event
     */
    @EventHandler
    public void instantUpdate(UpdatePlayerRoleEvent event)
    {
        playerList(event.player);
    }

    /**
     * Update how players are displayed.
     *
     * @param mode The new mode
     */
    public void mode(Mode mode)
    {
        if (this.mode != mode)
        {
            this.mode = mode;
            Players.stream().forEach(this::playerList);
        }
    }

    /**
     * Update how a player is displayed in the player list.
     *
     * @param player The player
     */
    private void playerList(Player player)
    {
        val role = database.cacheFetch(player.getUniqueId()).role();

        if (mode == Mode.ROLES)
            player.setPlayerListName((role == PLAYER ? PLAYER.color.toString() : LIST_FORMAT.apply(role)) + GREEN + player.getName());
        else
            player.setPlayerListName(GRAY + player.getName());
    }

    /**
     * How this handler operates
     */
    public enum Mode
    {
        ROLES, NO_ROLES
    }

}
