package net.neogamesmc.common.mongo.entities;

import org.mongodb.morphia.annotations.Embedded;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/19/2017 (10:26 AM)
 */
@Embedded
public class Settings
{

    public boolean flight;

    public boolean incognito;

    public boolean messages;

}
