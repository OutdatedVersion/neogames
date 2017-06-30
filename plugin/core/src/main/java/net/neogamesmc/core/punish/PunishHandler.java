package net.neogamesmc.core.punish;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.mutate.Mutator;
import net.neogamesmc.common.database.mutate.Mutators;
import net.neogamesmc.common.database.operation.InsertOperation;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.punish.payload.PunishmentPayload;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (1:36 AM)
 */
@Singleton
@ParallelStartup
public class PunishHandler
{

    public static final String SQL_FETCH_RECORD = "";

    public static final String SQL_RECORD_PUNISHMENT = "INSERT INTO punishments (target_id, issued_by, type, reason, expires_at) VALUES ((SELECT iid FROM accounts WHERE name=?), ?, ?, ?, ?);";

    /**
     * Our Redis instance.
     */
    private RedisHandler redis;

    /**
     * Our database instance.
     */
    private Database database;

    @Inject
    public PunishHandler(Database database, RedisHandler redis)
    {
        this.database = database;
        this.redis = redis.registerHook(this);

        Mutators.register(PunishmentType.class, new Mutator<PunishmentType>()
        {
            @Override
            public PunishmentType from(String fieldName, ResultSet result) throws SQLException
            {
                return PunishmentType.valueOf(result.getString(fieldName));
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
        final Player target = Bukkit.getPlayerExact(payload.targetPlayer);

        if (target != null)
        {
            payload.type.performAction(target, payload);
        }
    }


    public void issue(UUID issuedBy, String target, PunishmentType type, long duration, String... reason)
    {
        try
        {
            System.out.println("Hit #issue");
            final Instant adjustedTime = Instant.now().plus(duration, ChronoUnit.MILLIS);
            final AtomicInteger id = new AtomicInteger();

            System.out.println("Directly before insert");
            new InsertOperation(SQL_RECORD_PUNISHMENT)
                        .data(target, issuedBy, type, reason, adjustedTime)
                        .keys(result -> id.lazySet(result.getInt("id")))
                        .async(database);

            // TODO(Ben): get() makes this blocking?
            System.out.println("Before redis");
            new PunishmentPayload(id.get(), type, target, adjustedTime.toEpochMilli(), reason).publish(redis);
        }
        catch (Exception ex)
        {
            Issues.handle("Issue Punishment", ex);
        }
    }

}
