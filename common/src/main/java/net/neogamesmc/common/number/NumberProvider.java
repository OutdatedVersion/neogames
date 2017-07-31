package net.neogamesmc.common.number;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (6:18 PM)
 */
public class NumberProvider
{

    // TODO(Ben): primitive fastutil queue

    /**
     * The ports returned from old server instances.
     */
    private Queue<Integer> returned = new ConcurrentLinkedQueue<>();

    /**
     * Expose a way to incrementally retrieve ports.
     */
    private AtomicInteger working;

    /**
     * Create a new creator with the provided starting value.
     *
     * @param startingValue The starting value.
     */
    public NumberProvider(int startingValue)
    {
        this.working = new AtomicInteger(startingValue);
    }

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
        return returned.isEmpty() ? working.getAndIncrement() : returned.poll();
    }

    /**
     * Adds a port into our return pool.
     *
     * @param num The port
     * @return This provider
     */
    public NumberProvider returnNumber(int num)
    {
        // TODO(Ben): special case determined by why it failed

        if (!returned.add(num))
            throw new RuntimeException("Failed to return number to provider " + num);

        return this;
    }

}
