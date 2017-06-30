package net.neogamesmc.core.punish;

import com.google.inject.Inject;
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
    public static final Pattern REGEX = Pattern.compile("([0-9]*)((?i)[dhmw]+)");

    /**
     * Our managing instance.
     */
    @Inject private PunishHandler handler;

    @Command ( executor = "ban" )
    @Permission ( "punish.command.ban" )
    public void banCommand(Player player, @Necessary ( "Please provide the target's name" ) String name,
                                          @Necessary ( "A duration for the punishment is required" ) String duration,
                                          @Necessary ( "You must supply a reason" ) String[] reason)
    {
        handler.issue(player.getUniqueId(), name, PunishmentType.BAN,
                      duration.equalsIgnoreCase("perm") ? -1 : parseTime(duration),
                      reason);

        // /ban Nokoa 1w Being bad
    }

    @Command ( executor = "mute" )
    @Permission ( "punish.command.mute" )
    public void muteCommand(Player player, @Necessary ( "Please provide the target's name" ) String name,
                                           @Necessary ( "A duration for the punishment is required" ) String duration,
                                           @Necessary ( "You must supply a reason" ) String[] reason)
    {
        handler.issue(player.getUniqueId(), name, PunishmentType.MUTE,
                      duration.equalsIgnoreCase("perm") ? -1 : parseTime(duration),
                      reason);

        // /mute Nokoa 1y Talking about trains too much
    }

    @Command ( executor = "kick" )
    @Permission ( "punish.command.kick" )
    public void kickCommand(Player player, @Necessary ( "A target name must be provided" ) String name,
                                           @Necessary ( "You must supply a reason" ) String[] reason)
    {
        System.out.println("Hit method");
        handler.issue(player.getUniqueId(), name, PunishmentType.KICK, -1, reason);
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
        // 1w2d
        final Matcher matcher = REGEX.matcher(in);

        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid time provided");

        long result = 0;

        while (matcher.find())
        {
            result += PunishTools.parseTime(Integer.valueOf(matcher.group(1)), matcher.group(2).charAt(0));
        }

        return result;
    }

}
