package net.neogamesmc.common.task;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/22/2017 (11:29 PM)
 */
public interface Callback<Type>
{

    /**
     * Will be executed when the operation has completed successfully.
     *
     * @param val Whatever it is that we were looking for
     */
    void success(Type val);

    /**
     * Will be executed when the operation has failed to complete without issue.
     *
     * @param throwable The issue thrown
     */
    void failure(Throwable throwable);

}
