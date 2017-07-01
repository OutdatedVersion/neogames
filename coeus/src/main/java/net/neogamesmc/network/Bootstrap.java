package net.neogamesmc.network;

import net.neogamesmc.network.util.Constant;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.labelers.TimestampLabeler;
import org.pmw.tinylog.policies.DailyPolicy;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.RollingFileWriter;

import static org.pmw.tinylog.Logger.info;

/**
 * In-charge of doing the initial creation
 * of the network manager and its dependencies.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/27/2017 (3:54 PM)
 */
public class Bootstrap
{

    /**
     * JVM entry point
     *
     * @param args Runtime provided arguments
     */
    public static void main(String[] args)
    {
        Configurator.currentConfig()
                    .formatPattern("[{date:MM/dd hh:mm/ss aa}] [{thread:|min-size=8}] {level} : {message|indent=4}")
                    .writer(new ConsoleWriter())
                    .addWriter(new RollingFileWriter("network-manager.log.txt", 2, new TimestampLabeler(), new DailyPolicy()))
                    .writingThread(true)
                    .activate();

        info("Coeus");
        info("Version: {}", Constant.VERSION);
        info("Build: {}", "Need to inject Git SHA1");

        // boot a proxy
        // start spinning up lobbies
        // then game servers
    }

}
