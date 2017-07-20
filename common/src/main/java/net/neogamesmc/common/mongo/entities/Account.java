package net.neogamesmc.common.mongo.entities;

import lombok.Getter;
import lombok.Setter;
import net.neogamesmc.common.reference.Role;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;


import java.util.List;
import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/19/2017 (1:02 AM)
 */
@Entity ( "accounts" )
@Indexes ( {
       @Index ( value = "i_uuid", unique = true, fields = @Field ( "uuid" ) ),
       @Index ( value = "i_name", fields = @Field ( "name" ) )
} )
@Getter @Setter
public class Account
{

    @Id
    private ObjectId id;

    private UUID uuid;

    private String name;

    private Role role;

    @Property ( "address_current" )
    private String addressCurrent;

    @Property ( "address_previous" )
    private List<String> addressPrevious;

    @Property ( "name_previous" )
    private List<String> namePrevious;

    private Settings settings;

}
