package net.neogamesmc.core.npc;

import gnu.trove.list.array.TIntArrayList;

import java.util.concurrent.ThreadLocalRandom;

/**
 * OutdatedVersion
 * Dec/30/2016 (3:22 PM)
 * Assigns IDs for man ually created entities.
 */

public class EntityIDs
{

    /** the instance of this manager for this server */
    private static EntityIDs Instance;

    /** a collection featuring our IDs */
    private final TIntArrayList IN_USE = new TIntArrayList();

    /**
     * @return the only instance of this manager
     */
    public static EntityIDs get()
    {
        if (Instance == null)
            EntityIDs.Instance = new EntityIDs();

        return Instance;
    }

    /**
     * @return a random number to use as an entity ID
     */
    public int assignID()
    {
        int _working = fetchInt();

        while (IN_USE.contains(_working))
            _working = fetchInt();

        IN_USE.add(_working);
        return _working;
    }

    /**
     * @param val what we're checking for
     * @return whether or not we're currently using that value
     */
    public boolean inUse(int val)
    {
        return IN_USE.contains(val);
    }

    /**
     * @return a random integer
     */
    private int fetchInt()
    {
        return ThreadLocalRandom.current().nextInt(50_000) + 200_000;
    }

}
