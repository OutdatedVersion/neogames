package net.neogamesmc.core.command;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.neogamesmc.common.backend.ServerData;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.payload.SendDiscordMessagePayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BugCommand {
    @Inject
    private Database database;

    @Inject
    private RedisHandler redisHandler;

    @Inject
    private Plugin plugin;

    @Inject
    private ServerData serverData;

    private final long CHANNEL_ID = 333088112471179264l;

    @Command(executor = "bug")
    public void run(Player player, @Necessary("Invalid usage! Valid usage: /bug (description)") String[] desc) {
        try
                (
                        InputStream stream0 = plugin.getResource("build_data.json");
                        InputStreamReader stream = new InputStreamReader(stream0)
                ) {
            final VersionCommand.BuildData data = new Gson().fromJson(stream, VersionCommand.BuildData.class);
            Date date = new Date(System.currentTimeMillis());
            DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
            String dateFormatted = formatter.format(date);

            String message = ":bug:  New bug reported in server **" + serverData.name + "**, using build **" + data.build + "**\n**Description: **" + Text.convertArray(desc) + " -" + player.getName() + "\n**Server Time:** " + dateFormatted;
            Message.prefix("Bug").content("Bug report sent to developers, thank you", ChatColor.GREEN).send(player);
            new SendDiscordMessagePayload(CHANNEL_ID, message).publish(redisHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
