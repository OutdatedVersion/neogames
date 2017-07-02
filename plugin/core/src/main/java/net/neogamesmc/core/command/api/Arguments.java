package net.neogamesmc.core.command.api;

import java.util.Iterator;
import java.util.Optional;

/**
 * @author Ben (OutdatedVersion)
 * @since Mar/01/2017 (4:20 PM)
 */
public class Arguments implements Iterator<String>, Cloneable
{

    /** what the player typed */
    private final String[] raw;

    /** where we currently are in the raw arguments */
    private int currentPosition;

    /**
     * @param raw the elements
     */
    public Arguments(String[] raw)
    {
        this.raw = raw;
    }

    /**
     * @param raw the elements
     * @param position where we are
     */
    private Arguments(String[] raw, int position)
    {
        this.raw = raw;
        this.currentPosition = position;
    }

    /**
     * @return the next element in our arguments
     */
    @Override
    public String next()
    {
        return raw[currentPosition++];
    }

    /**
     * @return the next item in our arguments
     *         wrapped in an optional
     */
    public Optional<String> nextSafe()
    {
        return (raw.length > currentPosition + 1)
               ? Optional.empty()
               : Optional.of(raw[currentPosition++]);
    }

    /**
     * @return whether or not there's another
     *         element contained here
     */
    @Override
    public boolean hasNext()
    {
        return raw.length < currentPosition + 1;
    }

    /**
     * @return where we currently are in the arguments
     */
    public int currentPosition()
    {
        return currentPosition;
    }

    /**
     * @return how many places are left in the args
     */
    public int remainingElements()
    {
        return raw.length - currentPosition;
    }

    @Override
    public Arguments clone()
    {
        return new Arguments(this.raw, this.currentPosition);
    }

}
