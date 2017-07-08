package net.neogamesmc.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Singleton;
import lombok.val;
import net.neogamesmc.common.reference.Paths;
import net.neogamesmc.network.api.ConnectedServer;
import net.neogamesmc.network.data.GroupData;
import net.neogamesmc.network.deploy.DeployType;
import net.neogamesmc.network.task.CopyFileTask;
import net.neogamesmc.network.task.Task;
import net.neogamesmc.network.util.PortProvider;
import org.pmw.tinylog.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.pmw.tinylog.Logger.error;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/27/2017 (3:53 PM)
 */
@Singleton
public class Coeus
{

    /**
     * Run deployment tasks asynchronously.
     */
    private ListeningExecutorService executor = MoreExecutors.listeningDecorator(
            Executors.newCachedThreadPool(
                    new ThreadFactoryBuilder().setNameFormat("deployment-#%d").build()
            )
    );

    /**
     * Counter for grabbing server IDs.
     */
    private AtomicInteger serverID = new AtomicInteger(1);

    /**
     * Grab an avaiable port.
     */
    private PortProvider provider = new PortProvider();

    /**
     * Relation of group IDs to data pertaining to them.
     */
    private ConcurrentMap<String, GroupData> groupData = Maps.newConcurrentMap();

    /**
     *
     */
    private ServerDataBroker broker = new ServerDataBroker();

    public Coeus deploy(DeployType type, String group)
    {
        Logger.info("Coeus#deploy");

        executor.submit(() ->
        {
            try
            {
                val internal = new ConnectedServer();

                internal.id = serverID.getAndIncrement();
                Logger.info("Assigned ID: {}", internal.id);

                internal.port = provider.get();
                Logger.info("Assigned Port: {}", internal.port);

                internal.group = group;

                val data = groupData.computeIfAbsent(group, ignored -> new GroupData());

                internal.name = group.replaceAll("_", "") + data.serverCount.getAndIncrement();
                internal.dir = Paths.SERVERS.fileAt(String.valueOf(internal.id)).getAbsolutePath();
                Logger.info("Name: {} || Dir: {}", internal.name, internal.dir);

                switch (group)
                {
                    case "chunk_runner":
                        internal.maxPlayers = 14;
                        break;

                    case "blast_off":
                        internal.maxPlayers = 12;
                        break;

                    case "bowplinko":
                        internal.maxPlayers = 24;
                        break;
                }

                internal.onlinePlayers = 0;

                for (Task task : type.tasks(internal))
                    task.target(internal.dir).execute();

                if (group.equalsIgnoreCase("lobby"))
                {
                    new CopyFileTask(Paths.PLUGIN.fileAt("bukkit/Lobby.jar")).target(internal.dir).execute();
                }
                else
                {
                    new CopyFileTask(Paths.PLUGIN.fileAt("bukkit/Game-Server.jar")).target(internal.dir).execute();
                }

                broker.record(internal);
                Logger.info("[Deploy] Deployed {} to main-network", internal.name);
            }
            catch (Exception ex)
            {
                error(ex, "Issue encountered whilst provisioning server.");
            }
        });

        // add server to tracked map
        // add to group relation
        // save onto redis
        // proxy needs to track player joining/leaving (all together and each server)

        return this;
    }

    public <T> ListenableFuture<T> async(Callable<T> task)
    {
        return executor.submit(task);
    }

}
