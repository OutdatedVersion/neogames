package net.neogamesmc.discord.hook;

import net.dv8tion.jda.core.entities.Guild;
import net.neogamesmc.common.backend.SendDiscordMessagePayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.redis.api.HandlesType;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/02/2017 (12:20 AM)
 */
public class RedisRequestProcessor
{

    /**
     * An instance of the wrapper.
     */
    private final Guild guild;

    /**
     * Class Constructor
     *
     * @param redis Redis instance
     * @param guild JDA instance
     */
    public RedisRequestProcessor(RedisHandler redis, Guild guild)
    {
        this.guild = guild;
        redis.registerHook(this);
    }

    @HandlesType ( SendDiscordMessagePayload.class )
    public void sendMessages(SendDiscordMessagePayload payload)
    {
        guild.getTextChannelById(payload.channel).sendMessage(payload.content).queue();
    }

}
