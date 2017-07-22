package net.neogamesmc.common.account;

import net.neogamesmc.common.mongo.entities.Account;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/22/2017 (12:52 PM)
 */
public enum Setting
{

    /**
     * The ability to (safely) fly in lobbies.
     */
    LOBBY_FLIGHT(false),

    /**
     * The ability to be hidden from our general player base.
     */
    INCOGNITO(false),

    /**
     * The ability to receive private messages.
     */
    MESSAGES(true);

    /**
     * The key this setting will be saved under on our database.
     */
    public final String key;

    /**
     * The default value of this setting.
     */
    public final boolean defaultValue;

    /**
     * Create a setting representation with this constant's lower-case name as the database key.
     */
    Setting(boolean defaultValue)
    {
        this.key = this.name().toLowerCase();
        this.defaultValue = defaultValue;
    }

    /**
     * Create a setting representation with a custom name as the database key.
     *
     * @param val The name
     */
    Setting(String val, boolean defaultValue)
    {
        this.key = val;
        this.defaultValue = defaultValue;
    }

    /**
     * Invert the value for the provided setting on the account supplied.
     *
     * @param setting The setting to inverse
     * @param account The account to modify
     */
    public static void invert(Setting setting, Account account)
    {
        account.setting(setting, !account.setting(setting));
    }

}
