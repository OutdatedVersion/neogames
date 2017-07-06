package net.neogamesmc.buycraft.transaction;

import lombok.ToString;
import net.neogamesmc.buycraft.TransactionProcessor;

/**
 * Represents some purchase on the store.
 *
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (1:24 AM)
 */
@ToString
public abstract class Transaction
{

    /**
     * UUID of the player who made the purchase.
     */
    public final String uuid;

    /**
     * Username of the player.
     */
    public final String name;

    /**
     * Type of this purchase.
     */
    public final String type;

    /**
     * Data for this purchase.
     */
    public final String[] data;

    /**
     * Class Constructor
     *
     * @param uuid The player's UUID
     * @param name The player's username
     * @param type Transaction type
     * @param data Data for this transaction
     */
    public Transaction(String uuid, String name, String type, String... data)
    {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.data = data;
    }

    /**
     * Handle the processing of this purchase.
     *
     * @param processor Our processor
     */
    public abstract void process(TransactionProcessor processor);

}
