package net.neogamesmc.common.task;

import net.neogamesmc.common.exception.SentryHook;

import java.util.concurrent.Callable;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/22/2017 (11:29 PM)
 */
@FunctionalInterface
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
     * <p>
     * The default implementation sends the cause of the issue to our error tracker.
     * Though, you may wish to handle it differently on a per-use basis.
     *
     * @param throwable The issue thrown
     */
    default void failure(Throwable throwable)
    {
        SentryHook.report(throwable);
    }

    /**
     * Attempt executing the provided task, and passing it through to
     * the callback provided; in the event that the operation fails, the
     * {@link #failure(Throwable)} method of the callback is executed instead.
     *
     * @param callback The callback
     * @param callable The task to run
     * @param <T> Type-parameter for the task
     */
    static <T> void process(Callback<T> callback, Callable<T> callable)
    {
        try
        {
            callback.success(callable.call());
        }
        catch (Exception ex)
        {
            callback.failure(ex);
        }
    }

}
