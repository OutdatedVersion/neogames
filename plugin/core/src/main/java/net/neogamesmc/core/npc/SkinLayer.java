package net.neogamesmc.core.npc;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/10/2017 (12:03 AM)
 */
public enum SkinLayer
{

    // From: http://wiki.vg/Entities#Player

    CAPE(0),
    HAT(6),
    JACKET(1),
    PANTS_LEFT(4),
    PANTS_RIGHT(5),
    SLEEVE_LEFT(2),
    SLEEVE_RIGHT(3);

    public static final SkinLayer[] VALUES = values();

    final int flag;

    SkinLayer(int flag)
    {
        this.flag = 1 << flag;
    }

}
