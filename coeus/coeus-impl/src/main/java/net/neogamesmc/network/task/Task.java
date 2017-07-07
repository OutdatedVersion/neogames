package net.neogamesmc.network.task;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (3:17 AM)
 */
public interface Task
{

    Task target(String val);

    /**
     *
     */
    void execute() throws Exception;

}
