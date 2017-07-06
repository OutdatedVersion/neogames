package net.neogamesmc.core.punish;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Commands related to the issuing of punishments.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (1:13 AM)
 */
public class PunishCommands
{

    /**
     * Match the time argument in these commands.
     */
    public static final Pattern REGEX = Pattern.compile("([0-9]+)((?i)[dhmw])");

    /**
     * Our managing instance.
     */
    @Inject private PunishmentHandler handler;

    @Command ( executor = "ban" )
    @Permission ( Role.MOD )
    public void banCommand(Player player, @Necessary ( "Please provide the target's name" ) String name,
                                          @Necessary ( "A duration for the punishment is required" ) String duration,
                                          @Necessary ( "You must supply a reason" ) String[] reason)
    {
        handler.issue(player, name, PunishmentType.BAN, duration,
                      duration.equalsIgnoreCase("perm") ? -1 : parseTime(duration),
                      Text.convertArray(reason));

        // /ban Nokoa 1w Being bad
    }

    @Command ( executor = "mute" )
    @Permission ( Role.MOD )
    public void muteCommand(Player player, @Necessary ( "Please provide the target's name" ) String name,
                                           @Necessary ( "A duration for the punishment is required" ) String duration,
                                           @Necessary ( "You must supply a reason" ) String[] reason)
    {
        handler.issue(player, name, PunishmentType.MUTE, duration,
                      duration.equalsIgnoreCase("perm") ? -1 : parseTime(duration),
                      Text.convertArray(reason));

        // /mute Nokoa 1y Talking about trains too much
    }

    @Command ( executor = "kick" )
    @Permission ( Role.ADMIN )
    public void kickCommand(Player player, @Necessary ( "A target name must be provided" ) String name,
                                           @Necessary ( "You must supply a reason" ) String[] reason)
    {
        handler.issue(player, name, PunishmentType.KICK, null, -1, Text.convertArray(reason));
    }

    /**
     * Calculates how long the player provided in
     * {@code milliseconds} from the provided raw text.
     *
     * @param in The raw text
     * @return The calculated time
     * @throws IllegalArgumentException In the event that an invalid unit is provided
     */
    private static long parseTime(String in) throws IllegalArgumentException
    {
        Iterable<String> groups = Splitter.fixedLength(2).split(in);
        long result = 0;

        for (String val : groups)
        {
            final Matcher matcher = REGEX.matcher(val);

            if (!matcher.matches())
                throw new IllegalArgumentException("Invalid time provided");

            result += PunishTools.parseTime(Integer.valueOf(matcher.group(1)), matcher.group(2).charAt(0));
        }

        return result;
    }

}
