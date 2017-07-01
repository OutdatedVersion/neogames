package net.neogamesmc.buycraft;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.neogamesmc.buycraft.transaction.RoleTransaction;
import net.neogamesmc.buycraft.transaction.Transaction;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/01/2017 (1:23 AM)
 */
@Singleton
public class HookCommand extends Command
{

    /**
     * Our transaction processor instance.
     */
    @Inject private TransactionProcessor processor;

    /**
     * Class Constructor
     */
    public HookCommand()
    {
        super("hook", "console");
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (sender instanceof ConsoleCommandSender)
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
        }
    }

}
