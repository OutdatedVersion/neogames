package net.neogamesmc.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.neogamesmc.common.config.ConfigurationProvider;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.discord.data.BotConfiguration;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/02/2017 (12:08 AM)
 */
public class Bootstrap
{

    /**
     * JVM entry point.
     *
     * @param args Runtime provided arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            final BotConfiguration config = new ConfigurationProvider().read("discord/standard", BotConfiguration.class);

            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(config.token)
                    .setStatus(config.presence.status)
                    .setGame(Game.of(config.presence.text))
                    .buildBlocking();

            RedisHandler redis = new RedisHandler().init().subscribe(RedisChannel.DEFAULT, RedisChannel.NETWORK);
            new DiscordBot(jda, redis);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Unable to provision Discord bot.", ex);
        }
    }

}
