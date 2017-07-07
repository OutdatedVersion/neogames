package net.neogamesmc.network.deploy;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/27/2017 (5:14 PM)
 */
public class Group
{

    /**
     * A unique identifier for this group.
     * <p>
     * Example: {@code chunk_runner}
     */
    public String id;

    /**
     * A human-friendly name for this group.
     * <p>
     * Example: {@code Chunk Runner}
     */
    public String name;

    /**
     * Path to the directory containing the data we require.
     */
    public String dir;

    /**
     * Player related value bounds.
     */
    public PlayerData players;

    public static class PlayerData
    {
        public int min;
        public int max;
    }

}
