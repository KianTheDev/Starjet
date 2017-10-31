package me.thekian.cstmobs;

import net.minecraft.server.v1_11_R1.GenericAttributes;
import net.minecraft.server.v1_11_R1.EntitySilverfish;
import net.minecraft.server.v1_11_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_11_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_11_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_11_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_11_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_11_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_11_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_11_R1.SoundEffect;
import net.minecraft.server.v1_11_R1.SoundEffects;

public class CustomEntityEvilCow extends EntitySilverfish
{
	
	public CustomEntityEvilCow(net.minecraft.server.v1_11_R1.World world) 
	{
		super(world);
	}

	@Override
	protected void initAttributes() 
	{
		super.initAttributes();
		getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1D);
		getAttributeInstance(GenericAttributes.maxHealth).setValue(100D);
	}
	
	@Override
	protected void r()
	{

		goalSelector.a(1, new PathfinderGoalFloat(this));
		goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1D, false));
		goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.80000000000000004D));
		goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, net.minecraft.server.v1_11_R1.EntityHuman.class, 8F));
		goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
		targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
		targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, net.minecraft.server.v1_11_R1.EntityHuman.class, true));
	}
	
	@Override
	protected SoundEffect G()
	{
		return SoundEffects.am;
	}
}
