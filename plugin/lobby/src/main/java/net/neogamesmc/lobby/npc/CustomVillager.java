package net.neogamesmc.lobby.npc;

import net.minecraft.server.v1_11_R1.*;
import net.neogamesmc.lobby.NPC;

import java.util.Set;

/**
 * Created by nokoa on 6/30/2017.
 */
public class CustomVillager extends EntityVillager {
    public CustomVillager(World world) {
        super(world);

        Set goalB = (Set) NPC.privateField("b", PathfinderGoalSelector.class, goalSelector);
        goalB.clear();
        Set goalC = (Set) NPC.privateField("c", PathfinderGoalSelector.class, goalSelector);
        goalC.clear();
        Set targetB = (Set) NPC.privateField("b", PathfinderGoalSelector.class, targetSelector);
        targetB.clear();
        Set targetC = (Set) NPC.privateField("c", PathfinderGoalSelector.class, targetSelector);
        targetC.clear();


    }

    @Override
    protected void r() {
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 100.0F));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(1000);
    }

    @Override

    public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {

    }
}
