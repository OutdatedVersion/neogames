package net.neogamesmc.core.auth;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.operation.RawFetchOperation;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.issue.Issues;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * In-charge of applying user permissions; which
 * are loaded from the database when the server
 * is being initialized.
 * <p>
 * Every permission node a {@link Role} may possess
 * is defined within a table titled {@code assigned_permissions}
 * on our database instance.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/25/2017 (2:41 AM)
 */
@Singleton
@ParallelStartup
public class Permissions implements Listener
{

    /**
     * SQL statement to retrieve the information we require.
     */
    private static final String SQL_FETCH_NODES = "SELECT * FROM assigned_permissions;";

    /**
     * A collection of every permission node mapped to a role.
     */
    private Multimap<Role, String> nodes;

    /**
     * Local database instance.
     */
    private Database database;

    /**
     * Local plugin instance.
     */
    @Inject private Plugin plugin;

    /**
     * Loads permission nodes from our database
     * and stores them in-memory for quick access.
     */
    @Inject
    public Permissions(Database database)
    {
        try
        {
            this.database = database;

            nodes = MultimapBuilder.enumKeys(Role.class).hashSetValues().build();

             new RawFetchOperation(SQL_FETCH_NODES).task(set ->
             {
                 while (set.next())
                 {
                     nodes.put(Role.valueOf(set.getString("possessor")), set.getString("node"));
                 }
             }).sync(database);
        }
        catch (Exception ex)
        {
            Issues.handle("Fetch Permission Nodes", ex);
        }
    }

    @EventHandler ( priority = EventPriority.MONITOR )
    public void applyPermissions(PlayerLoginEvent event)
    {
        val player = event.getPlayer();
        val attachment = player.addAttachment(plugin);

        nodes.get(database.cacheFetch(player.getUniqueId()).role()).forEach(node -> attachment.setPermission(node, true));
    }

}
