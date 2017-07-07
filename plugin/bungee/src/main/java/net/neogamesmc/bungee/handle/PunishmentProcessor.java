package net.neogamesmc.bungee.handle;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import lombok.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.neogamesmc.bungee.NeoGames;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.operation.RawFetchOperation;
import net.neogamesmc.common.payload.PunishmentPayload;
import net.neogamesmc.common.payload.RequestProxyActionPayload;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.time.TimeFormatting;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static net.md_5.bungee.api.ChatColor.*;
import static net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.NONE;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/02/2017 (10:33 PM)
 */
public class PunishmentProcessor implements Listener
{

    /**
     * SQL query to fetch every active ban on a player's account.
     * <p>
     * We limit the result's quantity as we only really need one
     * active ban to deny their login.
     */
    private static final String SQL_ACTIVE_BANS = "SELECT id,reason,expires_at FROM punishments WHERE target=? AND revoked != 1 AND type='BAN' AND (expires_at IS NULL OR expires_at > NOW()) ORDER BY expires_at DESC LIMIT 1;";

    /**
     * The reason send when denying a login due to an issue on our side.
     */
    private static final BaseComponent[] REASON_FAILED = new ComponentBuilder("Unable to process proxy login -- Please report this.").color(RED).create();

    /**
     * An empty data value for our cache.
     */
    private static final PunishmentCacheData EMPTY_DATA = new PunishmentCacheData(0, null, null);

    /**
     * Reduce database calls/time when players log in.
     */
    private final LoadingCache<UUID, PunishmentCacheData> DATA_CACHE = CacheBuilder.newBuilder()
            .maximumSize(650)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<UUID, PunishmentCacheData>()
            {
                @Override
                public PunishmentCacheData load(UUID uuid) throws Exception
                {
                    val ref = new AtomicReference<PunishmentCacheData>();

                    new RawFetchOperation(SQL_ACTIVE_BANS).data(uuid).task(result ->
                            ref.set(result.next() ? new PunishmentCacheData(result.getInt(1), result.getString(2), result.getTimestamp(3))
                                                  : EMPTY_DATA)
                    ).sync(database);

                    return ref.get();
                }
            });

    /**
     * An instance of our plugin.
     */
    @Inject private NeoGames plugin;

    /**
     * Local database instance.
     */
    @Inject private Database database;

    @Inject
    public void init(RedisHandler redis)
    {
        redis.registerHook(this);
    }

    /**
     * Process login requests.
     *
     * @param event The event
     */
    @EventHandler
    @SneakyThrows
    public void interceptLogin(LoginEvent event)
    {
        if (!event.isCancelled())
        {
            event.registerIntent(plugin);

            plugin.async(() ->
            {
                try
                {
                    val data = DATA_CACHE.get(event.getConnection().getUniqueId());

                    if (data.present())
                    {
                        val builder = new ComponentBuilder("You have an active ban on your account!\n\n").color(RED).bold(true);

                        if (data.expiresAt == null)
                        {
                            builder.append("This sentence does not expire.", NONE).color(YELLOW);
                        }
                        else
                        {
                            builder.append("It's due to expire ", NONE).color(GRAY)
                                   .append(TimeFormatting.format(data.expiresAt.toInstant())).color(YELLOW);
                        }

                        builder.append("\nReason: ").color(GRAY)
                               .append(data.reason).color(WHITE)
                               .append("\nBan ID: ").color(GRAY)
                               .append("#" + data.id).color(WHITE);

                        denyLogin(event, builder.create());
                    }

                    event.completeIntent(plugin);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();

                    // TODO(Ben): bugsnag - perhaps put an issue ID in the reason?
                    denyLogin(event, REASON_FAILED);
                    event.completeIntent(plugin);
                }
            });
        }
    }

    // @HandlesType ( PunishmentPayload.class )
    public void updateCache(PunishmentPayload payload)
    {
        // TODO(Ben): IMPORTANT. Handle cache invalidation on punishment revoke.
    }

    @FromChannel ( RedisChannel.NETWORK )
    @HandlesType ( RequestProxyActionPayload.class )
    public void purgeCache(RequestProxyActionPayload payload)
    {
        if (payload.action == RequestProxyActionPayload.Action.PURGE_PUNISHMENT_CACHE)
        {
            DATA_CACHE.invalidateAll();
            System.out.println("[Network] Fulfilled purge punishment cache request.");
        }
    }

    /**
     * Holds login blocking punishment data.
     */
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class PunishmentCacheData
    {
        final int id;
        final String reason;
        final Timestamp expiresAt;

        boolean present()
        {
            return id != 0;
        }
    }

    /**
     * Deny the passed in login with the provided reason.
     *
     * @param event The event to deny
     * @param reason The reason for doing so
     */
    private static void denyLogin(LoginEvent event, BaseComponent[] reason)
    {
        event.setCancelled(true);
        event.setCancelReason(reason);
    }

}
