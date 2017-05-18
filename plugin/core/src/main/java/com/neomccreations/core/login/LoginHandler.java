package com.neomccreations.core.login;

import com.google.inject.Inject;
import com.neomccreations.common.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * Processes players joining servers.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (11:24 PM)
 */
public class LoginHandler implements Listener
{

    /**
     * Bridge to player data.
     */
    @Inject private Database database;

    @EventHandler
    public void handle(AsyncPlayerPreLoginEvent event)
    {

    }

}
