package net.neogamesmc.buycraft.transaction;

import net.neogamesmc.buycraft.TransactionProcessor;
import net.neogamesmc.common.payload.TransactionNoticePayload;
import net.neogamesmc.common.database.operation.InsertUpdateOperation;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (1:47 AM)
 */
public class RoleTransaction extends Transaction
{

    /**
     * SQL statement to update a player's role.
     */
    public static final String SQL_UPDATE_ROLE = "UPDATE accounts SET role=? WHERE uuid=?;";

    /**
     * Class Constructor
     *
     * @param uuid The player's UUID
     * @param name The player's name
     * @param role The player's role
     */
    public RoleTransaction(String uuid, String name, String role)
    {
        super(uuid, name, "ROLE", role);
    }

    @Override
    public void process(TransactionProcessor processor)
    {
        // Persistent data update
        processor.database(new InsertUpdateOperation(SQL_UPDATE_ROLE)
                                    .data(data[0], uuid));

        // Allow real-time in-game update if the player is online
        processor.publish(new TransactionNoticePayload(name, type, data));
    }

}
