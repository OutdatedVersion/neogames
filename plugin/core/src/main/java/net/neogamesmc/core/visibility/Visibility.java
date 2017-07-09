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
     * The player is the only person visible.
     */
    ONLY_SELF

}
