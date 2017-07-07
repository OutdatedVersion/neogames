package net.neogamesmc.common.backend;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.File;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (2:16 AM)
 */
@Data
public class ServerConfiguration
{

    /**
     * The file holding this content.
     */
    public static final File DATA_FILE = new File("server_data.json");

    /**
     * Name of this server.
     */
    public final String name;

    /**
     * Whether or not to automatically add/remove this server to/from the network.
     */
    @SerializedName ( "interact_with_network" )
    public boolean interactWithNetwork = true;

}
