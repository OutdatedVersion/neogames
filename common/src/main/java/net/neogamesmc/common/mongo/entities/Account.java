package net.neogamesmc.common.mongo.entities;

import com.mongodb.DBObject;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import lombok.EqualsAndHashCode;
import lombok.val;
import net.neogamesmc.common.account.Setting;
import net.neogamesmc.common.reference.Role;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.Set;
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
@EqualsAndHashCode
public class Account
{

    /**
     * Internal account ID.
     */
    @Id
    private ObjectId id;

    /**
     * A unique <i>constant</i> identifier assigned by Mojang.
     */
    private UUID uuid;

    /**
     * A unique player chosen username displayed publicly apart of their identity.
     */
    private String name;

    /**
     * The {@link Role} of this user.
     */
    private Role role;

    /**
     * UNIX epoch timestamp of the first time this player joined the network.
     */
    @Property ( "login_first" )
    private long loginFirst;

    /**
     * The last time the player logged into the network.
     * <p>
     * Switching servers does NOT count as a login, only proxy hits are counted.
     */
    @Property ( "login_last" )
    private long loginLast;

    /**
     * The current IP we are seeing the player from.
     * <p>
     * When this changes it will be relocated to {@link #addressPrevious} and this is updated to the new address.
     */
    @Property ( "address_current" )
    private String addressCurrent;

    /**
     * A collection of every IP address the player has joined the network with, excluding their current.
     */
    @Property ( "address_previous" )
    private Set<String> addressPrevious;

    /**
     * A collection of all the previous usernames the player represented by this account
     * has joined the network with.
     */
    @Property ( "name_previous" )
    private Set<String> namePrevious;

    /**
     * Relation of overridden {@link Setting}s for this account.
     * <p>
     * Only <i>changes</i> are tracked via this mapping. In other words,
     * only non-default {@link Setting} values are persisted on our database.
     * in some sort of attempt to reduce account size footprints.
     */
    private Object2BooleanMap<String> settings = new Object2BooleanArrayMap<>(Setting.values().length);

    /**
     * Grab the current value of a {@link Setting} for the player represented
     * by this account. In the case that their is no entry for the provided
     * setting the default-value is returned.
     *
     * @param setting The setting
     * @return The value
     */
    public boolean setting(Setting setting)
    {
        return settings.containsKey(setting.key) ? settings.getBoolean(setting.key)
                                                 : setting.defaultValue;
    }

    /**
     * Update the state of a setting locally.
     *
     * @param setting The setting
     * @param val The new value
     * @return This account, intended for chaining
     *
     * @see Account#settings Details regarding the behavior of user-settings
     * @see Setting#invert(Setting, Account) Utility method regarding setting updates
     */
    public Account setting(Setting setting, boolean val)
    {
        if (setting.defaultValue == val)
            settings.removeBoolean(setting.key);
        else
            settings.put(setting.key, val);

        return this;
    }

    /**
     * Tasks to execute immediately before this entity is saved to our database instance.
     *
     * @param object The {@link DBObject} representing this entity
     */
    @PreSave
    public void preSave(DBObject object)
    {
        // Lowercase version of the player's name
        // Used when querying accounts by username
        object.put("name_lower", name.toLowerCase());


        // Insert user setting overrides, if they exist
        if (!settings.isEmpty())
            settings.forEach((key, val) -> object.put("settings." + key, val));
    }

    /**
     * Tasks to execute before the fetched database values are assigned to this entity.
     *
     * @param object The {@link DBObject} representing this entity
     */
    @PreLoad
    public void preLoad(DBObject object)
    {
        val doc = (Document) object.get("settings");

        if (!doc.isEmpty())
            doc.forEach((key, val) -> settings.put(key, (boolean) val));
    }

}
