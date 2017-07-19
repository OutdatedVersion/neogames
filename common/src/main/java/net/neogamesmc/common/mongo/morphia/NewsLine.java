package net.neogamesmc.common.mongo.morphia;

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

    public String val;

    @Property ( "last_updated_at" )
    public long lastUpdatedAt;

    @Property ( "last_updated_by" )
    public Account lastUpdatedBy;

}
