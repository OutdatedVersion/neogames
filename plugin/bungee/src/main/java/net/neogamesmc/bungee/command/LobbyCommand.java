package net.neogamesmc.bungee.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.neogamesmc.bungee.distribution.PlayerDirector;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/15/2017 (11:08 AM)
 */
public class LobbyCommand extends Command
{

    /**
     * Access methods on actually sending players.
     */
    @Inject private PlayerDirector director;

    /**
     *
     */
    public LobbyCommand()
    {
        super("lobby", "", "hub", "leave");
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (sender instanceof ProxiedPlayer)
        {
            director.sendPlayer((ProxiedPlayer) sender, "lobby");
        }
    }

}
