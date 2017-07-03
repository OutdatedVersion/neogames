package net.neogamesmc.common.backend;

import lombok.Data;

import java.io.File;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/03/2017 (2:16 AM)
 */
@Data
public class ServerData
{

    /**
     * The file holding this content.
     */
    public static final File DATA_FILE = new File("server_data.json");

    /**
     * Name of this server.
     */
    public final String name;

}
