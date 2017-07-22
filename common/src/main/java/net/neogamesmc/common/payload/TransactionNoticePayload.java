package net.neogamesmc.common.payload;

import lombok.AllArgsConstructor;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (1:56 AM)
 */
@Focus ( "transaction-notice" )
@AllArgsConstructor
public class TransactionNoticePayload implements Payload
{

    /**
     * Name of the player who made the transaction.
     */
    public final String name;

    /**
     * The type of transaction performed.
     */
    public final String type;

    /**
     * Data around this transaction.
     */
    public final String[] data;

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
