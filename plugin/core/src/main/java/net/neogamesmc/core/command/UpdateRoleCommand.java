package net.neogamesmc.core.command;

import com.google.inject.Inject;
import lombok.val;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.payload.UpdatePlayerRolePayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.event.UpdatePlayerRoleEvent;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.scheduler.Scheduler;
import net.neogamesmc.core.text.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

import static net.md_5.bungee.api.ChatColor.RED;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (3:22 PM)
 */
public class UpdateRoleCommand
{

    /**
     * Local database instance.
     */
    @Inject private Database database;

    /**
     * Local Redis instance.
     */
    private RedisHandler redis;

    @Inject
    public void init(RedisHandler redis)
    {
        this.redis = redis.registerHook(this);
    }

    /**
     * Command to adjust other player's roles.
     *
     * @param player The player executing this command
     * @param targetName Name of the target
     * @param role The target role
     */
    @Command ( executor = "role" )
    @Permission ( Role.ADMIN )
    public void execute(Player player,
                        @Necessary ( "Please provide a target name" ) String targetName,
                        Role role)
    {
        val fetch = database.fetchAccount(targetName);

        fetch.addListener(() ->
        {
            try
            {
                val optional = fetch.get();

                if (!optional.isPresent())
                {
                    Message.noAccount(player, targetName);
                    return;
                }

                val account = optional.get();

                if (role == null)
                {
                    Message.prefix("Account").player(account.name()).content("currently possess")
                            .content(Text.fromEnum(account.role()), account.role().color).send(player);
                    return;
                }

                // role of the player who executed the command
                val ourRole = database.cacheFetch(player.getUniqueId()).role();

                // Don't allow any changes past the player's own role
                if (role.compare(ourRole))
                {
                    Message.prefix("Permissions").content("You may only set roles below your own", RED).send(player);
                    return;
                }

                // Detour players attempting to update some
                // account possessing a role ranked higher than them.
                if (account.role().compare(ourRole))
                {
                    Message.prefix("Permissions").content("You may not alter that player's role", RED).send(player);
                    return;
                }

                account.role(role, database, redis);

                Message.prefix("Account").content("You have updated the role of")
                        .player(account.name())
                        .content("to")
                        .content(Text.fromEnum(role), role.color)
                        .send(player);
            }
            catch (Exception ex)
            {
                Message.FAILED_TO_EXECUTE.send(player);
                Issues.handle("Role Command Execution", ex);
            }
        }, Scheduler::sync);
    }

    /**
     * Update player roles in real-time.
     *
     * @param payload The payload
     */
    @HandlesType ( UpdatePlayerRolePayload.class )
    public void updateLocally(UpdatePlayerRolePayload payload)
    {
        Optional.ofNullable(database.cacheFetch(payload.name)).ifPresent(account ->
        {
            val current = account.role();
            val player = Bukkit.getPlayer(payload.name);

            account.unsafeRole(payload.role);

            // Inform server
            event(player, current, account.role());

            // Inform human
            Message.prefix("Account").content("Your role has been updated to")
                                     .content(Text.fromEnum(payload.role), payload.role.color)
                                     .send(player);
        });
    }

    /**
     * Publish a Bukkit event to notify features of role changes.
     *
     * @param player The player
     * @param previous The previous role
     * @param fresh The new role
     */
    private static void event(Player player, Role previous, Role fresh)
    {
        new UpdatePlayerRoleEvent(player, fresh, previous).callEvent();
    }

}
