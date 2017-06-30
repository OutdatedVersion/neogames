package net.neogamesmc.core.punish;

import com.google.inject.Inject;
import net.neogamesmc.core.command.api.Command;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

/**
 * Commands related to the issuing of punishments.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (1:13 AM)
 */
public class PunishCommand
{

    /**
     * Match the time argument in these commands.
     */
    public static final Pattern REGEX = Pattern.compile("([0-9]*)((?i)[dhmwy]+)");

    /**
     * Our managing instance.
     */
    @Inject private Punish punish;

    @Command ( executor = "ban" )
    public void banCommand(Player player)
    {
        // /ban Nokoa 1w Being bad
    }

    @Command ( executor = "mute" )
    public void muteCommand(Player player)
    {
        // /mute Nokoa 1y Talking about trains too much
    }

}
