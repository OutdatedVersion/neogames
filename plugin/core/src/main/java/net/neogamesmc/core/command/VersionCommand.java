package net.neogamesmc.core.command;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Retrieved information relating to
 * the version of players.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (10:04 PM)
 */
public class VersionCommand
{

    /**
     * The pre-built message we'll be sending.
     */
    private Message message;

    /**
     * Class Constructor
     *
     * @param plugin A plugin instance
     */
    @Inject
    public VersionCommand(Plugin plugin)
    {
        try
        (
            InputStream stream0 = plugin.getResource("build_data.json");
            InputStreamReader stream = new InputStreamReader(stream0)
        )
        {
            final BuildData data = new Gson().fromJson(stream, BuildData.class);

            message = Message.start().content("NeoGames", ChatColor.GOLD).bold(true)
                           .newLine().content("Build Version: ", ChatColor.GRAY).content(data.build, ChatColor.YELLOW)
                           .newLine().content("Built/deployed by: ").content(data.by, ChatColor.YELLOW);
        }
        catch (Exception ex)
        {
            Issues.handle("Loading Build Data (Version Command)", ex);
        }
    }

    @Command ( executor = { "ver", "version", "build", "about" } )
    public void run(Player player)
    {
        message.sendAsIs(player);
    }

    /**
     * Data pertaining to build information.
     */
    public static class BuildData
    {
        @SerializedName ( "build_version" )
        public String build;

        @SerializedName ( "built_by" )
        public String by;
    }

}
