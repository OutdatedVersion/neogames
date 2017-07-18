package net.neogamesmc.core.punish;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.mutate.Mutator;
import net.neogamesmc.common.database.mutate.Mutators;
import net.neogamesmc.common.database.operation.InsertUpdateOperation;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.payload.PunishmentPayload;
import net.neogamesmc.common.payload.SendDiscordMessagePayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.scheduler.Scheduler;
import net.neogamesmc.core.text.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static net.neogamesmc.core.punish.PunishmentType.valueOf;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (1:36 AM)
 */
@Singleton
@ParallelStartup
public class PunishmentHandler
{

    /**
     * SQL statement to insert punishments.
     */
    private static final String SQL_RECORD_PUNISHMENT = "INSERT INTO punishments (target, target_id, issued_by, type, reason, expires_at) VALUES ((SELECT uuid FROM accounts WHERE name=?), (SELECT iid FROM accounts WHERE name=?), ?, ?, ?, ?);";

    /**
     * Discord channel ID for sending tracking messages.
     */
    private static final long CHANNEL_ID = 330950197368848397L;

    /**
     * Our Redis instance.
     */
    private RedisHandler redis;

    /**
     * Our database instance.
     */
    private Database database;

    @Inject
    public PunishmentHandler(Database database, RedisHandler redis)
    {
        this.database = database;
        this.redis = redis.registerHook(this);

        Mutators.register(PunishmentType.class, new Mutator<PunishmentType>()
        {
            @Override
            public PunishmentType from(String fieldName, ResultSet result) throws SQLException
            {
                return valueOf(result.getString(fieldName));
            }

            @Override
            public void to(PunishmentType data, int index, PreparedStatement statement) throws SQLException
            {
                statement.setString(index, data.name());
            }
        });
    }

    @FromChannel ( RedisChannel.DEFAULT )
    @HandlesType ( PunishmentPayload.class )
    public void handle(PunishmentPayload payload)
    {
        final Player target = Bukkit.getPlayerExact(payload.targetName);

        if (target != null)
        {
            Scheduler.sync(() -> valueOf(payload.type).performAction(target, payload));
        }
    }

    /**
     * Issue a punishment using the provided data.
     *
     * @param issuedBy The player who issued this punishment
     * @param target Who it is against
     * @param type Punishment type
     * @param duration Punishment length
     * @param reason The reason for doing this
     */
    public void issue(Player issuedBy, String target, PunishmentType type, String durationText, long duration, String reason)
    {
        Scheduler.async(() ->
        {
            try
            {
                final Instant adjustedTime = duration == -1 ? null : Instant.now().plus(duration, ChronoUnit.MILLIS);
                final AtomicInteger id = new AtomicInteger();

                new InsertUpdateOperation(SQL_RECORD_PUNISHMENT)
                        .data(target, target, issuedBy.getUniqueId(), type, reason, adjustedTime)
                        .keys(result ->
                        {
                            if (result.next())
                                id.set(result.getInt(1));
                        }).sync(database);

                val payload = new PunishmentPayload(id.get(), type.name(), target, duration == -1 ? -1 : adjustedTime.toEpochMilli(), reason);
                payload.publish(redis);

                new SendDiscordMessagePayload(CHANNEL_ID, type.message(payload, issuedBy.getName())).publish(redis);

                Message.prefix(Text.fromEnum(type)).content("You have issued a punishment against").player(target).send(issuedBy);
            }
            catch (Exception ex)
            {
                Issues.handle("Issue Punishment", ex);
            }
        });
    }

}
