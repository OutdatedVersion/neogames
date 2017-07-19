package net.neogamesmc.core.visibility;

import net.neogamesmc.common.reference.Role;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/09/2017 (1:31 AM)
 */
public enum Visibility
{

    /**
     * The player is visible to everyone.
     */
    TO_ALL,

    /**
     * The player will only be visible to player with their {@link Role} and higher.
     */
    ROLE_AND_HIGHER,

    /**
     * The player is only visible to staff members.
     * <p>
     * In other words, only to {@link Role#MOD} and above. Chances
     * are this will only be used within some sort of vanishing system.
     */
    ONLY_STAFF

}
