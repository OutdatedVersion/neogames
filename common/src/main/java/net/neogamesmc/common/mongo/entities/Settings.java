package net.neogamesmc.common.mongo.entities;

import lombok.Getter;
import lombok.Setter;
import org.mongodb.morphia.annotations.Embedded;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/19/2017 (10:26 AM)
 */
@Embedded
@Getter @Setter
public class Settings
{

    /**
     * Whether or not the player has lobby flight enabled.
     */
    public boolean flight;

    /**
     * Whether or not this player has vanished enabled.
     */
    public boolean incognito;

    /**
     * Whether or not this player may receive private messages from anyone.
     */
    public boolean messages;

}
