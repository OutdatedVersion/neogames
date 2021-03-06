package net.neogamesmc.common.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.annotation.Column;
import net.neogamesmc.common.database.annotation.InheritColumn;
import net.neogamesmc.common.database.annotation.Table;
import net.neogamesmc.common.database.operation.InsertUpdateOperation;
import net.neogamesmc.common.payload.UpdatePlayerRolePayload;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.reference.Role;

import java.time.Instant;
import java.util.UUID;

/**
 * Representation of a player's data.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (10:06 PM)
 */
@Table ( "accounts" )
@EqualsAndHashCode
@Getter
public class Account
{

    /**
     * SQL statement to update a player's account after they've logged in.
     */
    private static final String SQL_UPDATE_ACCOUNT = "UPDATE accounts SET name=?,address=?,last_login=? WHERE iid=?;";

    /**
     * Constant uniquely assigned identifier.
     *
     * <p>
     * The column's name is prefixed by an
     * {@code i} to indicate it as our internally
     * used ID. As opposed to {@link #uuid}.
     */
    @Column ( "iid" )
    public int id;

    /**
     * Constant Unique ID assigned by Mojang
     * to distinguish players.
     */
    @InheritColumn
    private UUID uuid;

    /**
     * Player chosen display name.
     */
    @InheritColumn
    private String name;

    /**
     * Permission/display role of this player.
     */
    @InheritColumn
    private Role role;

    /**
     * The amount of currency this player possesses.
     */
    @InheritColumn
    private int coins;

    /**
     * When the player was first seen here.
     */
    @Column ( "first_login" )
    private Instant firstLogin;

    /**
     * When the player was last seen on the network.
     */
    @Column ( "last_login" )
    private Instant lastLogin;

    /**
     * The last IP address we saw this player from.
     */
    @Column ( "address" )
    private String ip;

    // ---------------------------
    // Preferences

    /**
     * Whether or not the player has lobby flight enabled.
     */
    @Column ( "lobby_flight" )
    private boolean lobbyFlight;

    /**
     * Whether or not the player has private messaging enabled.
     */
    @Column ( "private_messages" )
    private boolean messages;

    /**
     * Create a new account from the provided login data.
     *
     * @param uuid The player's UUID
     * @param name The player's username
     * @param ip The player's IP address
     * @return The fresh account
     */
    public Account fromLogin(UUID uuid, String name, String ip)
    {
        // NEED TO ASSIGN ID
        this.role = Role.PLAYER;
        this.firstLogin = Instant.now();
        this.lastLogin = Instant.now();
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;

        return this;
    }

    /**
     * Update a player's account in our database
     * with data formed when logging in.
     *
     * @param database An instance of our database
     * @param name The new name
     * @param ip The new IP address
     * @return This account
     */
    public Account updateData(Database database, String name, String ip)
    {
        try
        {
            this.name = name;
            this.ip = ip;
            this.lastLogin = Instant.now();

            new InsertUpdateOperation(SQL_UPDATE_ACCOUNT)
                    .data(name, ip, lastLogin, id)
                    .sync(database);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Issue saving player data.", ex);
        }

        return this;
    }

    /**
     * Update the player's role.
     *
     * @param role The new role
     * @param database Our database instance
     * @param redis An instance of our Redis wrapper.
     * @return This account
     */
    @SneakyThrows
    public Account role(Role role, Database database, RedisHandler redis)
    {
        new InsertUpdateOperation("UPDATE accounts SET role=? WHERE iid=?;").data(role, id).async(database);
        new UpdatePlayerRolePayload(name, role).publish(redis);

        return unsafeRole(role);
    }

    /**
     * Update whether or not a player has lobby flight toggled.
     *
     * @param nowEnabled If it is now enabled
     * @param database Our database instance
     * @return This account
     */
    @SneakyThrows
    public Account lobbyFlight(boolean nowEnabled, Database database)
    {
        new InsertUpdateOperation("UPDATE settings SET lobby_flight=? WHERE account_id=?;").data(lobbyFlight = nowEnabled, id).async(database);

        return this;
    }

    /**
     * Update whether or not a player has private messaging toggled.
     *
     * @param nowEnabled If it is now enabled
     * @param database Our database instance
     * @return This account
     */
    @SneakyThrows
    public Account message(boolean nowEnabled, Database database)
    {
        new InsertUpdateOperation("UPDATE settings SET private_messages=? WHERE account_id=?;").data(messages = nowEnabled, id).async(database);

        return this;
    }

    /**
     * Modify the role value itself without any sort of
     * database updates/Redis notifications.
     *
     * @param role The new role
     * @return This account
     */
    public Account unsafeRole(Role role)
    {
        this.role = role;
        return this;
    }

}
