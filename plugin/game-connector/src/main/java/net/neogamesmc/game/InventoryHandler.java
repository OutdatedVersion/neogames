package net.neogamesmc.game;

import com.google.common.collect.Range;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Created by nokoa on 7/19/2017.
 */
public class InventoryHandler implements Listener {
    /**
     * Representation of all of the slots a player may not use.
     */
    private static final Range<Integer> SLOTS = Range.closed(1, 4);


    /**
     * Disallow people from interacting with their crafting inventories.
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void disallow(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.CRAFTING && SLOTS.contains(event.getRawSlot())) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        }
    }
}

