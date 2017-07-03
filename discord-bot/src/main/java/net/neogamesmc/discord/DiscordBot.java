package net.neogamesmc.discord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.discord.data.Constants;
import net.neogamesmc.discord.hook.RedisRequestProcessor;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/02/2017 (12:07 AM)
 */
public class DiscordBot
{

    /**
     * The only instance of our bot existing.
     */
    private final JDA jda;

    /**
     * Representation of the NeoGames Discord guild.
     */
    public final Guild guild;

    /**
     * Class Constructor
     *
     * @param bot Bot instance
     */
    public DiscordBot(JDA bot, RedisHandler redis)
    {
        this.jda = bot;

        this.guild = jda.getGuildById(Constants.GUILD_ID);

        new RedisRequestProcessor(redis, guild);
    }

}
