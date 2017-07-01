package net.neogamesmc.core.punish;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.punish.payload.PunishmentPayload;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

import static net.md_5.bungee.api.ChatColor.*;
import static net.neogamesmc.core.text.Colors.bold;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (6:43 AM)
 */
public enum PunishmentType
{

    /**
     * Removes the ability for a player to join the network.
     */
    BAN((player, data) ->
        player.kickPlayer(TextComponent.toLegacyText(
                new ComponentBuilder("A ban has been issued against your account.\n\n").color(RED)
                        .append("Expires " + PunishTools.FORMAT_LENGTH.apply(data)).color(YELLOW)
                        .append("\nReason: ").color(GRAY)
                        .append(Text.convertArray(data.reason)).color(WHITE)
                        .append("\nBan ID: ").color(GRAY)
                        .append("#" + data.id).color(WHITE)
                        .create()
        ))
    ),

    /**
     * Removes a player from the network, but allows them to rejoin.
     */
    KICK((player, data) ->
        player.kickPlayer(TextComponent.toLegacyText(
                new ComponentBuilder("You've been removed from the network.\n\n").color(RED)
                        .append("Reason: ").color(GRAY)
                        .append(Text.convertArray(data.reason)).color(WHITE)
                        .append("\nKick ID: ").color(GRAY)
                        .append("#" + data.id).color(WHITE)
                        .create()
        ))
    ),

    /**
     * Removes the ability to chat from a player.
     */
    MUTE((player, data) ->
    {
        player.sendMessage(bold(RED) + "You have been muted until " + bold(YELLOW) + PunishTools.FORMAT_LENGTH.apply(data));
        player.sendMessage(bold(GRAY) + "Reason: " + bold(WHITE) + Text.convertArray(data.reason));
    });


    /**
     * The action behind this type.
     */
    private final BiConsumer<Player, PunishmentPayload> action;

    /**
     * Class Constructor
     *
     * @param action The action
     */
    PunishmentType(BiConsumer<Player, PunishmentPayload> action)
    {
        this.action = action;
    }

    /**
     * Run the action backing this punishment type.
     *
     * @param player The player being punished.
     * @param payload The data
     */
    public void performAction(Player player, PunishmentPayload payload)
    {
        action.accept(player, payload);
    }

}
