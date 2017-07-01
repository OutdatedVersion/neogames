package net.neogamesmc.network;

import net.neogamesmc.common.reference.Paths;
import net.neogamesmc.network.data.GroupData;
import net.neogamesmc.network.data.ServerData;
import net.neogamesmc.network.deploy.DeployType;
import net.neogamesmc.network.task.Task;
import net.neogamesmc.network.util.PortProvider;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.pmw.tinylog.Logger.error;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/27/2017 (3:53 PM)
 */
public class Coeus
{

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
    private ConcurrentMap<String, GroupData> groupData;

    public Coeus deploy(DeployType type, String group)
    {
        try
        {
            final ServerData data = new ServerData();
            data.id = serverID.getAndIncrement();
            data.port = provider.get();
            data.group = group;
            data.name = group.replaceAll("_", "") + groupData.get(group).serverCount.getAndIncrement();
            data.dir = Paths.SERVERS.fileAt("" + data.id).getAbsolutePath();

            for (Task task : type.tasks(data))
            {
                task.target(data.dir);
                task.execute();
            }

            // add server to tracked map
            // add to group relation
            // save onto redis
            // proxy needs to track player joining/leaving (all together and each server)
        }
        catch (Exception ex)
        {
            error(ex, "Issue encountered whilst provisioning server.");
        }

        return this;
    }

}
