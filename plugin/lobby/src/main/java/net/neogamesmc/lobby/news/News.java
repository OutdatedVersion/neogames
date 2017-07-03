package net.neogamesmc.lobby.news;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import lombok.Data;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.operation.RawFetchOperation;
import net.neogamesmc.common.payload.RequestNewsRefreshPayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.scheduler.Scheduler;
import net.neogamesmc.core.text.Colors;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (11:47 AM)
 */
public class News implements Listener
{

    /**
     * SQL query to load all of our news lines.
     */
    private static final String SQL_FETCH_LINES = "SELECT news.*,accounts.name FROM news INNER JOIN accounts ON news.updated_by = accounts.iid;";

    /**
     * All of our current lines to use.
     */
    private Cache<Integer, NewsData> newsLines = CacheBuilder.newBuilder().build();

    /**
     * The ID of the currently displayed line.
     */
    private AtomicInteger currentLine = new AtomicInteger();

    /**
     * The bar we use to display the {@link #newsLines}.
     */
    private BossBar bar;

    @Inject
    public void init(Database database, RedisHandler redis)
    {
        fetchLines(database, this::updateLocalBar);
        redis.registerHook(this);

        Scheduler.timer(this::updateLocalBar, 20 * 10);
    }

    /**
     * Load the lines from the database.
     *
     * @param database Our database instance
     * @param callback The task to run when we've fetched the lines
     */
    private void fetchLines(Database database, Runnable callback)
    {
        try
        {
            newsLines.invalidateAll();

            new RawFetchOperation(SQL_FETCH_LINES).task(result ->
            {
                while (result.next())
                {
                    newsLines.put(result.getInt("id"), new NewsData(
                                    result.getString("val"),
                                    result.getString("name"),
                                    result.getTimestamp("last_updated_at").toInstant()
                                  ));
                }

                Scheduler.sync(callback);
            }).async(database);
        }
        catch (Exception ex)
        {
            Issues.handle("Fetch News Lines", ex);
        }
    }

    /**
     * Update our local news display to the next line.
     */
    private void updateLocalBar()
    {
        if (bar == null)
        {
            bar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);
            bar.setVisible(true);
        }

        bar.setTitle(grabLine(progressLine()));
    }

    /**
     * Increment our current index by one.
     *
     * @return The fresh ID
     */
    private int progressLine()
    {
        return currentLine.get() + 1 > newsLines.size() ? currentLine.getAndSet(1) : currentLine.incrementAndGet();
    }

    /**
     * Grab the provided line by ID from our cache.
     *
     * @param id ID of the line we're looking for
     * @return The colorized line
     */
    private String grabLine(int id)
    {
        return Colors.colorize(newsLines.getIfPresent(id).val);
    }

    @EventHandler
    public void addPlayer(PlayerJoinEvent event)
    {
        bar.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void removePlayer(PlayerQuitEvent event)
    {
        bar.removePlayer(event.getPlayer());
    }

    @HandlesType ( RequestNewsRefreshPayload.class )
    public void updateOnCommand(RequestNewsRefreshPayload payload)
    {
        newsLines.put(payload.id, new NewsData(payload.newValue, payload.updatedBy, Instant.ofEpochMilli(payload.updatedAt)));

        if (currentLine.get() == payload.id)
            bar.setTitle(grabLine(payload.id));
    }

    /**
     * Represents a single line of news.
     */
    @Data
    public static class NewsData
    {
        final String val;
        final String updatedBy;
        final Instant lastUpdate;
    }

}
