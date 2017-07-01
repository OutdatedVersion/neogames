package net.neogamesmc.core.command.api.satisfier;

import com.google.inject.Inject;
import net.neogamesmc.common.account.Account;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.core.command.api.ArgumentSatisfier;
import net.neogamesmc.core.command.api.Arguments;
import net.neogamesmc.core.command.api.data.OnlineOfflinePlayer;
import net.neogamesmc.core.player.Players;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicReference;

import static org.bukkit.Color.YELLOW;

/**
 * @author Ben (OutdatedVersion)
 * @since Mar/31/2017 (5:09 PM)
 */
public class AccountSatisfier implements ArgumentSatisfier<OnlineOfflinePlayer>
{

    /** need to access this */
    @Inject private Database database;

    @Override
    public OnlineOfflinePlayer get(Player player, Arguments args)
    {
        final String _name = args.next();
        Player _onlineTry = Players.find(player, _name, true);
        AtomicReference<Account> account = new AtomicReference<>();

        if (_onlineTry == null)
        {
            // Scheduler.async(() -> account.lazySet(database(player.getUniqueId())));
            // TODO(Ben): implement
        }

        return new OnlineOfflinePlayer(_onlineTry, account.get());
    }

    @Override
    public String fail(String provided)
    {
        return "Couldn't find any online/offline player matching: " + YELLOW + provided;
    }

    @Override
    public Class<OnlineOfflinePlayer> satisfies()
    {
        return OnlineOfflinePlayer.class;
    }

}
