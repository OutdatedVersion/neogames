package net.neogamesmc.network.deploy;

import net.neogamesmc.common.backend.UpdateNetworkServersPayload;
import net.neogamesmc.common.reference.Paths;
import net.neogamesmc.network.data.ServerData;
import net.neogamesmc.network.task.*;

import java.util.function.Function;

import static org.apache.commons.lang3.tuple.Pair.of;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/27/2017 (5:13 PM)
 */
public enum DeployType
{

    /**
     * a
     */
    ADD_PROXY(data -> null),

    /**
     * Creates a Minecraft server instance, and adds
     * it to our BungeeCord proxy.
     */
    ADD_SERVER(data -> new Task[] {
            new CopyDirectoryTask(Paths.STORAGE.fileAt("server-template")),
            new CopyFileTask(Paths.PLUGIN.fileAt("third-party/ViaVersion.jar")),
            new ReplaceVariablesTask("start.sh", of("ID", data.id)),
            new ReplaceVariablesTask("server.properties", of("PORT", data.port)),
            new WriteToFileTask("server_data.json", data),
            new ExecuteScriptTask("start.sh"),
            new PublishPayloadTask(new UpdateNetworkServersPayload(data.name, data.port))
    });

    /**
     * Return a set of tasks to execute using the provided data.
     */
    private Function<ServerData, Task[]> function;

    /**
     * Constructor
     *
     * @param function The function
     */
    DeployType(Function<ServerData, Task[]> function)
    {
        this.function = function;
    }

    /**
     * Grab our tasks.
     *
     * @param data The data
     * @return The tasks
     */
    public Task[] tasks(ServerData data)
    {
        return function.apply(data);
    }

}
