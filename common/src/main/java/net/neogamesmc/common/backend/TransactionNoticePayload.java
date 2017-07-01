package net.neogamesmc.common.backend;

import net.neogamesmc.common.json.JSONBuilder;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (1:56 AM)
 */
@Focus ( "transaction-notice" )
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

    /**
     * Class Constructor
     *
     * @param name The player's name
     * @param type Type of the transaction
     * @param data Data for this transaction
     */
    public TransactionNoticePayload(String name, String type, String[] data)
    {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    @Override
    public JSONObject asJSON()
    {
        return new JSONBuilder()
                        .add("name", name)
                        .add("type", type)
                        .add("data", data)
                        .done();
    }

    @Override
    public RedisChannel channel()
    {
        return RedisChannel.DEFAULT;
    }

}
