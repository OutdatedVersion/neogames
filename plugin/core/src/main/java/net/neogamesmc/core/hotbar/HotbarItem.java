package net.neogamesmc.core.hotbar;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (7:25 PM)
 */
public class HotbarItem
{

    /**
     *  The item stack behind this.
     */
    public ItemStack stack;

    /**
     * UUID of the player possessing this item.
     */
    public UUID uuid;

    /**
     * A collection of every executable action.
     */
    private Multimap<Action, Consumer<Player>> actions = MultimapBuilder.enumKeys(Action.class).arrayListValues().build();

    /**
     * Class Constructor
     *
     * @param player Player in possession of this item
     * @param stack The item
     */
    public HotbarItem(Player player, ItemStack stack)
    {
        this.uuid = player.getUniqueId();
        this.stack = stack;
    }

    public HotbarItem location(int loc)
    {
        return this;
    }

    /**
     * Assign the action to execute when we click
     * on this item.
     *
     * @param action The action
     * @return This item, for chaining
     */
    public HotbarItem action(Action type, Consumer<Player> action)
    {
        actions.put(type, action);
        return this;
    }

    /**
     * If we have handlers available for a certain
     * {@link ClickType} execute.
     *
     * @param type The type
     * @param player The player
     */
    void process(Action type, Player player)
    {
        Optional.ofNullable(actions.get(type)).ifPresent(set -> set.forEach(action -> action.accept(player)));
    }

}
