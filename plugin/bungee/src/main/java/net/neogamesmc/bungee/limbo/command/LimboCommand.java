package net.neogamesmc.bungee.limbo.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.neogamesmc.bungee.limbo.Limbo;

/**
 * @author Ben (OutdatedVersion)
 * @since Aug/24/2017 (4:34 PM)
 */
@Singleton
public class LimboCommand extends Command
{

    @Inject private Limbo manager;

    public LimboCommand()
    {
        super("limbo");
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        val player = ProxyServer.getInstance().getPlayer(sender.getName());

        manager.sendTo();
    }

}
