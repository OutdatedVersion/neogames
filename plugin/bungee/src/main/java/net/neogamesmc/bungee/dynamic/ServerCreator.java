package net.neogamesmc.bungee.dynamic;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.neogamesmc.bungee.NeoGames;
import net.neogamesmc.bungee.event.AddServerEvent;
import net.neogamesmc.common.backend.ServerConfiguration;
import net.neogamesmc.common.exception.SentryHook;
import net.neogamesmc.common.number.NumberProvider;
import net.neogamesmc.common.reference.Paths;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (6:15 PM)
 */
@Singleton
public class ServerCreator
{

    /**
     * Local GSON instance.
     */
    private static final Gson GSON = new Gson();

    /**
     * Our proxy instance.
     */
    @Inject private ProxyServer proxy;

    /**
     * Local plugin instance.
     */
    @Inject private NeoGames plugin;

    /**
     * Service to run these requests.
     */
    private ListeningExecutorService service = MoreExecutors.listeningDecorator(
            Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("deployment-%d").build())
    );

    /**
     * Grab a random ID.
     */
    private NumberProvider id = new NumberProvider(1);

    /**
     * Grab a random port.
     */
    private NumberProvider port = new NumberProvider(25566);

    /**
     * All servers we're currently tracking.
     * <p>
     * The key is the group ID.
     */
    private Map<String, GroupData> groups = Maps.newConcurrentMap();

    /**
     * All data waiting to be added.
     */
    private Map<String, ServerData> waitingToAdd = Maps.newConcurrentMap();

    /**
     * Provision a server in the provided group and start it.
     *
     * @param group The group
     * @return Future
     */
    public ListenableFuture<String> createAndStartServer(String group)
    {
        val future = createServer(group);

        future.addListener(() ->
        {
            try
            {
                val path = future.get();

                if (path == null)
                    throw new RuntimeException("No path returned from creation in " + group);

                Runtime.getRuntime().exec(new String[] { "/bin/sh", path } );
            }
            catch (Exception ex)
            {
                SentryHook.report(ex);
            }
        }, plugin::async);

        return future;
    }

    /**
     * Provision a server in the provided group.
     *
     * @param groupRaw The group
     * @return The path to the start up script
     */
    public ListenableFuture<String> createServer(String groupRaw)
    {
        return service.submit(() ->
        {
            try
            {
                val group = groupRaw.toLowerCase();
                val groupData = groups.computeIfAbsent(group, ignored -> new GroupData());

                // The server number in this group
                int groupNumber = groupData.idProvider.get();

                // Name of the server
                // Example: lobby1
                String name = group + groupNumber;

                // Check if we already have a server under this name
                while (proxy.getServersCopy().containsKey(name))
                {
                    System.out.println("[Network] Attempting next ID :: Already has " + name);

                    groupNumber = groupData.idProvider.get();
                    name = group + groupNumber;
                }

                val assignedID = id.get();
                val assignedPort = port.get();
                val dir = Paths.SERVERS.fileAt(String.valueOf(assignedID));
                val path = dir.getAbsolutePath();

                // copy template
                FileUtils.copyDirectory(Paths.STORAGE.fileAt("server-template"), dir);

                // replace vars in files
                val startFile = new File(path + "/start.sh");
                val propertiesFile = new File(path + "/server.properties");

                val replacedStart = FileUtils.readFileToString(startFile).replaceAll("ID", String.valueOf(assignedID))
                                                                         .replaceAll("MEM_MIN", "256M")
                                                                         .replaceAll("MEM_MAX", memMax(group) + "M");

                String propertiesContent = FileUtils.readFileToString(propertiesFile);
                String replacedProperties = propertiesContent.replaceAll("PORT", String.valueOf(assignedPort));

                // Special Case -- TEMP SOLUTION
                if (group.equals("mariokart"))
                {
                    replacedProperties = replacedProperties.replaceAll("resource-pack=", "resource-pack=https://assets.neogamesmc.net/maps/mariokart/pack/mariokart-3.zip");
                }

                // Write changes
                FileUtils.writeStringToFile(startFile, replacedStart);
                FileUtils.writeStringToFile(propertiesFile, replacedProperties);

                // Copy plugin
                if (group.equals("lobby"))
                    FileUtils.copyFileToDirectory(Paths.PLUGIN.fileAt("bukkit/Lobby.jar"), new File(path + "/plugins"));
                else
                    FileUtils.copyFileToDirectory(Paths.PLUGIN.fileAt("bukkit/Game-Connector.jar"), new File(path + "/plugins"));

                // Copy map
                // TODO(Ben): Move Bukkit side
                FileUtils.copyDirectory(Paths.STORAGE.fileAt("/maps/" + group), new File(path + "/lobby"));

                // Data for the server to ingest
                val maxPlayers = maxPlayersFromGroup(group);
                val config = new ServerConfiguration(assignedID, name, group, maxPlayers);

                // Write data file
                FileUtils.writeStringToFile(new File(path + "/server_data.json"), GSON.toJson(config));

                // Waiting to add
                waitingToAdd.put(name, new ServerData(assignedID, name, group, assignedPort, maxPlayers));

                // Update map with changes
                groups.put(group, groupData);

                System.out.println("[Network Provisioning] Deployed " + name + " [" + assignedID + ":" + assignedPort + "]");

                return startFile.getAbsolutePath();
            }
            catch (Exception ex)
            {
                SentryHook.report(ex);
                return null;
            }
        });
    }

    /**
     * Grab every server within the provided group.
     *
     * @param group The group ID
     * @return The servers or an empty set.
     */
    public Set<ServerData> serversInGroup(String group)
    {
        val data = groups.get(group);

        return data == null ? Collections.emptySet() : data.servers;
    }

    /**
     * Start tracking a server.
     *
     * @param name Name of the server
     */
    public void addServer(String name)
    {
        val data = waitingToAdd.remove(name);

        if (data != null)
        {
            proxy.getServers().put(name, proxy.constructServerInfo(name, new InetSocketAddress(data.port), null, false));
            groups.get(data.group).servers.add(data);

            // send out notification to local code
            proxy.getPluginManager().callEvent(new AddServerEvent(data));

            // "log"
            System.out.println("[Network] Now tracking " + name);
        }
    }

    /**
     * Stop tracking a server.
     *
     * @param group Group this server is in
     * @param name The server's name
     */
    @SneakyThrows
    public void removeServer(String group, String name)
    {
        val data = groups.get(group);
        int discardedID = Integer.parseInt(name.replace(group, ""));

        // Allow us to reuse that server ID
        data.idProvider.returnNumber(discardedID);

        val iterator = data.servers.iterator();
        ServerData server = null;

        // Remove from server list
        // TODO(Ben): O(n) -- replace w/ map
        while (iterator.hasNext())
        {
            val next = iterator.next();

            if (next.name.equalsIgnoreCase(name))
            {
                server = next;
                iterator.remove();
                break;
            }
        }

        // Cleanup from this server's lifetime
        if (server != null)
        {
            port.returnNumber(server.port);
            id.returnNumber(server.id);

            FileUtils.deleteDirectory(new File(Paths.SERVERS.path + "/" + server.id));

            // Remove from proxy
            proxy.getServers().remove(name);
            System.out.println("[Network] No longer tracking " + name);
        }
    }

    /**
     * TEMP.
     *
     * @return max players
     */
    public static int maxPlayersFromGroup(String group)
    {
        switch (group)
        {
            case "chunkrunner":
            case "bowplinko":
            case "mariokart":
                return 24;

            case "blastoff":
                return 12;

            case "fishingforgold":
                return 10;

            default:
                return 50;
        }
    }

    /**
     * Grab the maximum amount of memory for a group.
     *
     * @param group The group name
     * @return The value
     */
    private static short memMax(String group)
    {
        switch (group)
        {
            case "blastoff":
                return 756;

            default:
                return 512;
        }
    }

}
