package net.neogamesmc.buycraft;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.neogamesmc.buycraft.transaction.RoleTransaction;
import net.neogamesmc.buycraft.transaction.Transaction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (1:23 AM)
 */
@Singleton
public class HookCommand implements CommandExecutor
{

    /**
     * Our transaction processor instance.
     */
    @Inject private TransactionProcessor processor;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof ConsoleCommandSender)
        {
            if (label.equals("hook"))
            {
                Transaction transaction;

                switch (args[0])
                {
                    // Example: /hook ROLE 03c337cd-7be0-4694-b9b0-e2fd03f57258 OutdatedVersion NEW_ROLE
                    case "ROLE":
                        transaction = new RoleTransaction(args[1], args[2], args[3]);
                        break;

                    default:
                        throw new IllegalArgumentException("Invalid type provided to hook.");
                }

                processor.queue(transaction);
                System.out.println("[Payment Hook] Processing queued: " + transaction.toString());
            }
        }

        return true;
    }

}
