package net.neogamesmc.network.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (6:40 AM)
 */
public class PortProvider
{

    /**
     * The ports returned from old server instances.
     */
    private Queue<Integer> returnedPorts = new ConcurrentLinkedQueue<>();

    /**
     *
     */
    private AtomicInteger portWorking = new AtomicInteger(25566);

    /**
     * Grab a new port.
     *
     * @return The port
     */
    public int get()
    {
        return returnedPorts.isEmpty() ? returnedPorts.poll() : portWorking.getAndIncrement();
    }

}
