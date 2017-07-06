package net.neogamesmc.core.command.api.satisfier;

import net.neogamesmc.core.command.api.ArgumentSatisfier;
import net.neogamesmc.core.command.api.Arguments;
import org.bukkit.entity.Player;

/**
 * Returns whoever ran the command
 *
 * @author Ben (OutdatedVersion)
 * @since Mar/21/2017 (11:33 AM)
 */
public class ExecutedBySatisfier implements ArgumentSatisfier<Player>
{

    @Override
    public Player get(Player player, Arguments args)
    {
        return player;
    }

    @Override
    public String fail(String provided)
    {
        return "How in the hell did this fail? Please contact a developer with a picture of this.";
    }

    @Override
    public Class<Player> satisfies()
    {
        return Player.class;
    }

}
