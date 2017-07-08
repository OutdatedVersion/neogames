package net.neogamesmc.bungee.dynamic;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.inject.Inject;
import lombok.SneakyThrows;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.neogamesmc.bungee.util.NumberProvider;
import net.neogamesmc.common.backend.ServerConfiguration;
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

    // group: lobby
    public ListenableFuture<String> createServer(String group)
    {
        return service.submit(() ->
        {
            try
            {
                val groupData = groups.computeIfAbsent(group, ignored -> new GroupData());

                // The server number in this group
                val groupNumber = groupData.serverCount().incrementAndGet();

                // Name of the server
                // Example: lobby1
                val name = group + groupNumber;

                // Check if we already have a server under this name
                if (proxy.getServers().containsKey(name))
                {
                    System.out.println("[Network] Skipping server provision :: Already has " + name);
                    groupData.serverCount().decrementAndGet();
                    return null;
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

                val replacedStart = FileUtils.readFileToString(startFile).replaceAll("ID", String.valueOf(assignedID));
                val replacedProperties = FileUtils.readFileToString(propertiesFile).replaceAll("PORT", String.valueOf(assignedPort));

                // Write changes
                FileUtils.writeStringToFile(startFile, replacedStart);
                FileUtils.writeStringToFile(propertiesFile, replacedProperties);

                // Copy plugin
                if (group.equals("lobby"))
                    FileUtils.copyFileToDirectory(Paths.PLUGIN.fileAt("bukkit/Lobby.jar"), new File(path + "/plugins"));
                else
                    FileUtils.copyFileToDirectory(Paths.PLUGIN.fileAt("bukkit/Game-Server.jar"), new File(path + "/plugins"));

                // Copy map
                // TODO(Ben): Move Bukkit side
                FileUtils.copyDirectory(Paths.STORAGE.fileAt("/maps/" + group), new File(path + "/lobby"));

                // Data for the server to ingest
                val maxPlayers = maxPlayersFromGroup(group);
                val config = new ServerConfiguration(name, group, maxPlayers);

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
                ex.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Grab the total server count in the provided group.
     *
     * @param group The group ID
     * @return The server count
     */
    public int serverCountInGroup(String group)
    {
        val data = groups.get(group);

        System.out.println("Groups: " + groups.toString());
        System.out.println("data = " + (data == null ? null : data.toString()));

        return data == null ? 0 : data.serverCount.get();
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

        return data == null ? Collections.emptySet() : Collections.unmodifiableSet(data.servers);
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

        // Lower count by one
        data.serverCount.decrementAndGet();

        val iterator = data.servers.iterator();
        ServerData server = null;

        // Remove from server list
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
        }


        // Remove from proxy
        proxy.getServers().remove(name);
    }

    /**
     * TEMP.
     *
     * @return max players
     */
    private static int maxPlayersFromGroup(String group)
    {
        switch (group)
        {
            case "chunkrunner":
            case "bowplinko":
                return 24;

            case "blastoff":
                return 12;

            default:
                return 100;
        }
    }

}
