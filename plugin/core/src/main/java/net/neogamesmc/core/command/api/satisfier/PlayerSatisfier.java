package net.neogamesmc.core.command.api.satisfier;

import net.neogamesmc.core.command.api.ArgumentSatisfier;
import net.neogamesmc.core.command.api.Arguments;
import net.neogamesmc.core.player.Players;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Mar/21/2017 (9:28 PM)
 */
public class PlayerSatisfier implements ArgumentSatisfier<Player>
{

    @Override
    public Player get(Player player, Arguments args)
    {
        return Players.find(player, args.next(), true);
    }

    @Override
    public String fail(String provided)
    {
        return null;
    }

    @Override
    public Class<Player> satisfies()
    {
        return Player.class;
    }

}
