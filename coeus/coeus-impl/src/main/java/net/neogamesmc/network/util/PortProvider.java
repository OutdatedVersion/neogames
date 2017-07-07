package net.neogamesmc.network.util;

import org.pmw.tinylog.Logger;

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
    private Queue<Short> returnedPorts = new ConcurrentLinkedQueue<>();

    /**
     * Expose a way to incrementally retrieve ports.
     */
    private AtomicInteger portWorking = new AtomicInteger(25566);

    /**
     * Fetch a new port from our system.
     * <p>
     * Attempt to grab it from our returned queue first, then
     * just grab a new one if none are avaiable there.
     *
     * @return The port
     */
    public int get()
    {
        return returnedPorts.isEmpty() ? returnedPorts.poll() : portWorking.getAndIncrement();
    }

    /**
     * Adds a port into our return pool.
     *
     * @param port The port
     * @return This provider
     */
    public PortProvider returnPort(short port)
    {
        if (!returnedPorts.add(port))
            Logger.error("[Port Provider] Failed to add port {}", port);

        return this;
    }

}
