package net.neogamesmc.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.val;
import net.neogamesmc.common.reference.Paths;
import net.neogamesmc.network.api.ConnectedServer;
import net.neogamesmc.network.data.GroupData;
import net.neogamesmc.network.deploy.DeployType;
import net.neogamesmc.network.task.Task;
import net.neogamesmc.network.util.PortProvider;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.pmw.tinylog.Logger.error;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/27/2017 (3:53 PM)
 */
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
        executor.submit(() ->
        {
            try
            {
                val internal = new ConnectedServer();

                internal.id = serverID.getAndIncrement();
                internal.port = provider.get();
                internal.group = group;

                val data = groupData.computeIfAbsent(group, ignored -> new GroupData());

                internal.name = group.replaceAll("_", "") + data.serverCount.getAndIncrement();
                internal.dir = Paths.SERVERS.fileAt(String.valueOf(internal.id)).getAbsolutePath();

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

                broker.record(internal);
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
