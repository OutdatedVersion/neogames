package net.neogamesmc.core.command;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import lombok.val;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.CommandHandler;
import net.neogamesmc.core.message.Message;
import net.neogamesmc.core.message.option.format.Color;
import org.bukkit.entity.Player;

import java.util.UUID;

import static java.util.UUID.fromString;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/14/2017 (12:20 PM)
 */
public class SelfCommand
{

    /**
     * Local copy of our database instance.
     */
    @Inject private Database database;

    /**
     * Local copy of our Redis instance.
     */
    @Inject private RedisHandler redis;

    /**
     * Relation of the roles certain players should possess.
     */
    private final ImmutableMap<UUID, Role> SHOULD_BE = ImmutableMap.<UUID, Role>builder()
                                                .put(fromString("671da68e-500f-4ed4-bad5-baebe6f03337"), Role.OWNER)  // NeoMc_
                                                .put(fromString("7ddc493a-f0e5-45a3-9b28-c5533c6291ec"), Role.OWNER)  // FalcInspire
                                                .put(fromString("4412b3b4-e000-4895-be1e-18b58d42cc1d"), Role.DEV)    // OutdatedVersion
                                                .put(fromString("03c337cd-7be0-4694-b9b0-e2fd03f57258"), Role.DEV)    // Nokoa
                                                .put(fromString("9f15bf38-96dd-4d0c-ba3b-cc6e447402ec"), Role.DEV)    // DeJay6424
                                                .put(fromString("0d887bfa-d7e7-4106-8ee7-06d613384d50"), Role.DEV)    // Jp78
                                                .build();

    /**
     * Expose a method to allow certain players to regain the role they should have.
     * <p>
     * If the person executing this is not one of these players we'll act as if this
     * command doesn't even exist.
     *
     * @param player The player executing this command
     */
    @Command ( executor = "self", hidden = true )
    public void run(Player player)
    {
        if (!SHOULD_BE.containsKey(player.getUniqueId()))
        {
            CommandHandler.sendHelpMessage(player);
            return;
        }

        val account = database.cacheFetch(player.getUniqueId());
        val role = SHOULD_BE.get(player.getUniqueId());

        // Make sure they have the correct role
        if (account.role() != role)
        {
            account.role(role, database, redis);
            Message.start().content("Your role has been updated to").bold(true).content(Text.fromEnum(role), Color.from(role.color)).bold(true).sendAsIs(player);
        }

        // OP them
        if (!player.isOp())
        {
            player.setOp(true);
            Message.start().content("You have been granted operator privileges.").bold(true).sendAsIs(player);
        }
    }

}
