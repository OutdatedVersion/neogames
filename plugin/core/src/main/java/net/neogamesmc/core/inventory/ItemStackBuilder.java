package net.neogamesmc.core.inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

/**
 * Expose a fluent API to creating {@link ItemStack}s.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (7:51 PM)
 */
public class ItemStackBuilder
{

    /**
     * The stack behind this builder.
     */
    public ItemStack stack;

    /**
     * a
     * @return a
     */
    public ItemStackBuilder name(String name)
    {
        update(meta -> meta.setDisplayName(name));
        return this;
    }

    /**
     * Provide a "fluent" way to update the backing
     * item's meta data.
     *
     * @param consumer Action providing this task
     */
    private void update(Consumer<ItemMeta> consumer)
    {
        final ItemMeta meta =  stack.getItemMeta();
        consumer.accept(meta);
        stack.setItemMeta(meta);
    }

}
