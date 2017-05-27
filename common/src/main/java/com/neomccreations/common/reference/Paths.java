package com.neomccreations.common.reference;

import java.io.File;

/**
 * Merely holds data regarding
 * the location of things on our
 * servers.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (10:25 PM)
 */
public enum Paths
{

    /**
     * The base of where we store our stuff.
     */
    BASE(".mc"),

    /**
     * Where data related to properties for tools and such is held.
     */
    CONFIG(BASE + ".config"),

    /**
     * Where assorted servers are located.
     */
    NETWORK(BASE + ".network"),

    /**
     * Where our currently single BungeeCord proxy is.
     */
    PROXY(NETWORK + ".proxy"),

    /**
     * Where the individual Minecraft instances are.
     */
    SERVERS(NETWORK + ".live");

    /** The actual file system path */
    public final String path;

    /**
     * Constructor
     *
     * @param path The file system path. A {@code period} is used
     *             as a temporary path separator; then
     *             replaced by the current system's preferred
     *             separating character. Do NOT append a
     *             trailing slash to any of the above.
     */
    Paths(String path)
    {
        this.path = path.replaceAll("\\.", File.separator);
    }

    /**
     * Returns a {@link File} at the specified location.
     *
     * @param relativePath Path relative to the base this
     *                     particular enum represents.
     * @return The file
     */
    public File fileAt(String relativePath)
    {
        return new File(this.path + File.separator + relativePath);
    }

}
