package net.neogamesmc.buycraft;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.neogamesmc.buycraft.transaction.Transaction;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.api.Operation;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.Payload;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (1:27 AM)
 */
@Singleton
public class TransactionProcessor implements Runnable
{

    /**
     * Bridge to our database.
     */
    @Inject private Database database;

    /**
     * Bridge to our Redis instance.
     */
    @Inject private RedisHandler redis;

    /**
     * A queue of transactions that must be processed.
     */
    private final Queue<Transaction> transactionQueue = new ConcurrentLinkedQueue<>();

    /**
     * Process a transaction every time this task fires off.
     */
    @Override
    public void run()
    {
        if (!transactionQueue.isEmpty())
            transactionQueue.poll().process(this);
    }

    /**
     * Add a transaction to our processing queue.
     *
     * @param transaction The transaction
     * @return This processor, for chaining
     */
    public TransactionProcessor queue(Transaction transaction)
    {
        try
        {
            transactionQueue.add(transaction);
        }
        catch (IllegalArgumentException | IllegalStateException ex)
        {
            throw new RuntimeException("Failed to add transaction to queue.", ex);
        }

        return this;
    }

    /**
     * Run a database oriented task.
     *
     * @param operation The operation
     * @param <T> Type-parameter for this operation
     * @return A future with data from this task
     */
    public <T> Future<T> database(Operation<T> operation)
    {
        return database.submitTask(operation);
    }

    /**
     * Sends out the provided payload.
     *
     * @param payload The payload
     */
    public void publish(Payload payload)
    {
        payload.publish(redis);
    }

}
