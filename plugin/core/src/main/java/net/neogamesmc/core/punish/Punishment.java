package net.neogamesmc.core.punish;

import java.time.Instant;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/30/2017 (7:27 AM)
 */
public class Punishment
{

    public int id;
    public String reason;
    public Instant expiresAt;
    public PunishmentType type;

}
