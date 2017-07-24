package net.neogamesmc.common.mongo.entities;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.neogamesmc.common.mongo.converters.location.Location;
import org.mongodb.morphia.annotations.Entity;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/23/2017 (10:42 PM)
 */
@Entity ( "npcs" )
public class NPCData
{

    public String name;

    public Location location;

    public String type;

    public Object2ObjectMap<String, String> data = new Object2ObjectLinkedOpenHashMap<>();

}
