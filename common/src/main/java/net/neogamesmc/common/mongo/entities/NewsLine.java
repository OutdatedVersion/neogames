package net.neogamesmc.common.mongo.entities;

import lombok.Getter;
import lombok.Setter;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/19/2017 (10:41 AM)
 */
@Entity ( "news" )
@Getter @Setter
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
    @Property ( "last_updated_by" )
    public Account lastUpdatedBy;

}
