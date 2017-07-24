package net.neogamesmc.common.mongo.entities;

import net.neogamesmc.common.reference.Role;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/23/2017 (10:33 PM)
 */
@Entity ( "self_command_entries" )
public class SelfEntry
{

    /**
     * Identifier for who needs to be set.
     */
    public UUID uuid;

    /**
     * The role this person should be.
     */
    @Property ( "should_be" )
    public Role shouldBe;

}
