package net.neogamesmc.core.punish;

import java.time.Instant;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (7:27 AM)
 */
public class Punishment
{

    /**
     * ID of the punishment.
     */
    public int id;

    /**
     * Reason for this punishment being issued.
     */
    public String reason;

    /**
     * When this punishment expires.
     */
    public Instant expiresAt;

    /**
     * The type of this punishment.
     */
    public PunishmentType type;

}
