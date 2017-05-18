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
    BASE("/mc"),

    /**
     * Where data related to properties for tools and such is held.
     */
    CONFIG(BASE + "/config");

    /** The actual file system path */
    public final String path;

    /**
     * Constructor
     *
     * @param path The file system path
     */
    Paths(String path)
    {
        this.path = path;
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
        return new File(this.path + relativePath);
    }

}
