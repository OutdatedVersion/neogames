package net.neogamesmc.discord.data;

import net.dv8tion.jda.core.OnlineStatus;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/02/2017 (12:11 AM)
 */
public class BotConfiguration
{

    /**
     * Token for authorization with the Discord API.
     */
    public String token;

    /**
     * Game/status data for this bot.
     */
    public Presence presence;

    /**
     * Details regarding the bot's public-state.
     */
    public static class Presence
    {
        /**
         * Content to display where the currently playing game goes.
         */
        public String text;

        /**
         * Online status of our user.
         */
        public OnlineStatus status;
    }

}
