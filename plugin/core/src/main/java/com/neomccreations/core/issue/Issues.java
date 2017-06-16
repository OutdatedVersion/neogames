package com.neomccreations.core.issue;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 * Utility methods relating to the
 * processing of exceptions.
 *
 * @author Ben (OutdatedVersion)
 * @since May/20/2017 (2:34 PM)
 */
public class Issues
{

    /**
     * Displays the provided issue in-game to
     * those who may find it useful and print
     * the full trace to the server's output feed.
     *
     * @param friendly In-game display of what happened.
     *                 Make this simple and precise.
     * @param throwable Representation of the issue
     */
    public static void handle(String friendly, Throwable throwable)
    {
        // console
        throwable.printStackTrace();


        final StackTraceElement head = throwable.getStackTrace()[0];

        // ex
        // x ERROR java.lang.NPE <friendly>
        ComponentBuilder builder = new ComponentBuilder("x").obfuscated(true).bold(true).color(ChatColor.GRAY);

        builder.append(" ERROR ").color(ChatColor.DARK_RED).obfuscated(false);

        builder.append(throwable.toString()).color(ChatColor.GRAY);

        builder.append(friendly).color(ChatColor.RED);
    }

}
