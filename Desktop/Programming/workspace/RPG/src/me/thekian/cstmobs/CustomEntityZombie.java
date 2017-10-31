package me.thekian.cstmobs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.util.UnsafeList;

import net.minecraft.server.v1_11_R1.EntityZombie;
import net.minecraft.server.v1_11_R1.GenericAttributes;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_11_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_11_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_11_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_11_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_11_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_11_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_11_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_11_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_11_R1.PathfinderGoalZombieAttack;

public class CustomEntityZombie extends EntityZombie 
{
	
	public CustomEntityZombie(net.minecraft.server.v1_11_R1.World world) 
	{
		super(world);
		/*try 
		{
		} catch (Exception e) 
		{
			System.out.println("Error creating custom zombie entity.");
			e.printStackTrace();
		}*/
	}

	@Override
	protected void initAttributes() 
	{
		super.initAttributes();
		//getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(35D);
		//getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23000000417232513D);
		getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1D);
		getAttributeInstance(GenericAttributes.maxHealth).setValue(100D);
		//getAttributeInstance(GenericAttributes.g).setValue(2D);
		//getAttributeMap().b(a).setValue(random.nextDouble() * 0.10000000149011612D);
	}
	
	@Override
	protected void r()
	{
		//super.r();
		goalSelector.a(0, new PathfinderGoalFloat(this));
		goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
		goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
		goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
		goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8F));
		goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
		//o();
	}

	@Override
	protected boolean o()
	{
		//super.o();
		//goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, false));
		//targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[] {EntityPigZombie.class}));
		targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
		//if(world.spigotConfig.zombieAggressiveTowardsVillager)    targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, false));
		//	targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, true));
		return true;
	}
}
