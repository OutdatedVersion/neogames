package net.neogamesmc.common.collection;

import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;

/**
 * Create a map via a chaining builder. This
 * is nice for one line map creation.
 *
 * @author Ben (OutdatedVersion)
 * @since May/20/2017 (1:32 PM)
 */
public class MapBuilder<K, V>
{

    /** the backing map */
    private Map<K, V> map = Maps.newHashMap();

    /**
     * @param <K> type of the key
     * @param <V> type of the value
     * @return the fresh builder
     */
    public static <K, V> MapBuilder<K, V> builder()
    {
        return new MapBuilder<>();
    }

    /**
     * Introduces a new element into
     * our backing map
     *
     * @param key key of it
     * @param value value of it
     * @return this builder
     */
    public MapBuilder<K, V> add(K key, V value)
    {
        map.put(key, value);
        return this;
    }

    /**
     * @return the map behind this
     */
    public Map<K, V> regular()
    {
        return map;
    }

    /**
     * @return the map behind this, but
     *         unchangeable
     */
    public Map<K, V> unmodifiable()
    {
        return Collections.unmodifiableMap(map);
    }

}
