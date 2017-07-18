package net.neogamesmc.core.punish;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.neogamesmc.common.payload.PunishmentPayload;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

import static net.md_5.bungee.api.ChatColor.*;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.FORMATTING;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;

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
    {
        player.kickPlayer(TextComponent.toLegacyText(
                new ComponentBuilder("A ban has been issued against your account.\n\n").color(RED).bold(true)
                             .append("Expires " + PunishTools.format(data), NONE).color(YELLOW)
                             .append("\nReason: ").color(GRAY)
                             .append(data.reason).color(WHITE)
                             .append("\nBan ID: ").color(GRAY).append("#" + data.id).color(WHITE).create())
        );

        Message.start().content("A player has been removed from your server for abuse.", RED).bold(true).sendAsIs();
    }, ":hammer: -id **-target** has been banned by **-issued_by**. This will expire -expire.\n**Reason:** -reason"),

    /**
     * Removes a player from the network, but allows them to rejoin.
     */
    KICK((player, data) ->
        player.kickPlayer(TextComponent.toLegacyText(
                new ComponentBuilder("You've been removed from the network.\n\n").color(RED).bold(true)
                        .append("Reason: ", NONE).color(GRAY)
                        .append(data.reason).color(WHITE)
                        .append("\nKick ID: ").color(GRAY)
                        .append("#" + data.id).color(WHITE)
                        .create()
        ))
    , ":x: -id **-target** has been kicked from the network by **-issued_by** for -reason."),

    /**
     * Removes the ability to chat from a player.
     */
    MUTE((player, data) ->
        player.sendMessage(new ComponentBuilder("You have been muted until ").color(RED).bold(true)
                .append(PunishTools.format(data), FORMATTING).color(YELLOW)
                .append(".\n").color(RED)
                .append("Reason: ").color(GRAY)
                .append(data.reason).color(WHITE).create())
    , ":no_entry_sign: -id **-target** has been muted by **-issued_by**. Which will expire -expire.\n**Reason:** -reason");


    /**
     * The action behind this type.
     */
    private final BiConsumer<Player, PunishmentPayload> action;

    /**
     * The Discord emoji associated with this action.
     */
    public final String discordMessage;

    /**
     * Class Constructor
     *
     * @param discordMessage The emoji
     * @param action The action
     */
    PunishmentType(BiConsumer<Player, PunishmentPayload> action, String discordMessage)
    {
        this.action = action;
        this.discordMessage = discordMessage;
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

    /**
     * Returns a message formatted to our Discord standard.
     *
     * @param payload Punishment data
     * @param issuedBy Name of the player who punished the target
     * @return The message
     */
    public String message(PunishmentPayload payload, String issuedBy)
    {
        return this.discordMessage.replace("-target", payload.targetName)
                                  .replace("-issued_by", issuedBy)
                                  .replace("-expire", PunishTools.format(payload))
                                  .replace("-reason", payload.reason)
                                  .replace("-id", "__#" + payload.id + "__ | ");
    }

}
