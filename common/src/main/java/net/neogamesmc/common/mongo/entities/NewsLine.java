package net.neogamesmc.common.mongo.entities;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/19/2017 (10:41 AM)
 */
@Entity ( "news" )
@Setter
@EqualsAndHashCode @ToString
public class NewsLine
{

    /**
     * The un-parsed news line; Still includes color codes.
     */
    public String val;

    /**
     * The unix epoch timestamp for when this line was last updated.
     */
    @Property ( "last_updated_at" )
    public long lastUpdatedAt;

    /**
     * Reference to the player's account who performed the last change on this line.
     */
    @Reference ( value = "last_updated_by", lazy = true )
    public Account lastUpdatedBy;

}
