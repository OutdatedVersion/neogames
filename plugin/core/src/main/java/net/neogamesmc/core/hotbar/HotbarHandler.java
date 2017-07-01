package net.neogamesmc.core.hotbar;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (7:26 PM)
 */
@Singleton
public class HotbarHandler implements Listener
{

    /**
     * All of the currently active items.
     */
    private Multimap<UUID, HotbarItem> items = MultimapBuilder.hashKeys().hashSetValues().build();

    /**
     * Start tracking an item.
     *
     * @param item The item
     * @return This handler, for chaining
     */
    public HotbarHandler register(HotbarItem item)
    {
        items.put(item.player.getUniqueId(), item);
        return this;
    }

    /**
     * Stop tracking an item.
     *
     * @param item The item
     * @return This handler, for chaining
     */
    public HotbarHandler remove(HotbarItem item)
    {
        items.remove(item.player.getUniqueId(), item);
        return this;
    }

    /**
     * Handle the interaction of this event.
     *
     * @param event The event
     */
    @EventHandler
    public void handle(PlayerInteractEvent event)
    {
        final ItemStack hand = event.getItem();

        if (hand != null)
        {
            items.values().stream().filter(item -> item.stack.getItemMeta().getDisplayName().equals(hand.getItemMeta().getDisplayName()))
                                   .forEach(item -> item.process(event.getAction(), event.getPlayer()));
        }
    }

    /**
     * Remove entries as the possessing player leaves.
     *
     * @param event The event
     */
    @EventHandler
    public void cleanup(PlayerQuitEvent event)
    {
        items.removeAll(event.getPlayer().getUniqueId());
    }

}
