package net.neogamesmc.core.command.api;

import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Mar/19/2017 (10:03 AM)
 */
public interface ArgumentSatisfier<T>
{

    /**
     * Grabs something of type {@code T}
     * from the provided data
     *
     * @param player the player
     * @param args the args provided at execution
     * @return something of type T
     */
    T get(Player player, Arguments args);

    /**
     * @param provided what the player typed
     * @return the message to send when if
     *         we fail to do what {@link #get(Player, Arguments)}
     *         does
     */
    String fail(String provided);

    /**
     * @return the class we're satisfying
     */
    Class<T> satisfies();

}
