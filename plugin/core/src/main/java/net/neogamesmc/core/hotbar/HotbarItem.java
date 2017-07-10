package net.neogamesmc.core.hotbar;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (7:25 PM)
 */
public class HotbarItem
{

    /**
     * The player associated with this item.
     */
    public final Player player;

    /**
     *  The item stack behind this.
     */
    public final ItemStack stack;

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
        this.player = player;
        this.stack = stack;
    }

    /**
     * Set the item where it should be
     *
     * @param loc The slot number
     * @return The
     */
    public HotbarItem location(int loc)
    {
        player.getInventory().setItem(loc, stack);
        return this;
    }

    /**
     * Assign the action to execute when we click
     * on this item.
     *
     * @param action The action
     * @return This item, for chaining
     */
    public HotbarItem action(Consumer<Player> action, Action... types)
    {
        for (Action type : types)
            actions.put(type, action);

        return this;
    }

    /**
     * Add this item to the provided handler.
     *
     * @param handler The handler
     * @return This item, for chaining
     */
    public HotbarItem add(HotbarHandler handler)
    {
        handler.register(this);
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
