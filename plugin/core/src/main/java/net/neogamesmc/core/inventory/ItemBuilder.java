package net.neogamesmc.core.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Expose a fluent API to creating {@link ItemStack}s.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (7:51 PM)
 */
public class ItemBuilder
{

    /**
     * The stack behind this builder.
     */
    private ItemStack stack;

    /**
     * Class Constructor
     *
     * @param material The material to initialize with
     */
    public ItemBuilder(Material material)
    {
        this.stack = new ItemStack(material);
    }

    /**
     * Updates the display name of this item.
     *
     * @return This builder, for chaining
     */
    public ItemBuilder name(String name)
    {
        update(meta -> meta.setDisplayName(name));
        return this;
    }

    /**
     * Update the lore of this item.
     *
     * @param lines The lines
     * @return This builder, for chaining
     */
    public ItemBuilder lore(String... lines)
    {
        update(meta -> meta.setLore(Arrays.asList(lines)));
        return this;
    }

    /**
     * Finish off this builder
     *
     * @return The stack
     */
    public ItemStack build()
    {
        return stack;
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
