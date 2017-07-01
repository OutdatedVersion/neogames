package net.neogamesmc.core.scheduler;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (9:47 AM)
 */
public interface ExceptionRunnable
{

    /**
     * Execute this particular task.
     *
     * @throws Exception In the event that ANYTHING goes wrong.
     */
    void run() throws Exception;

}
