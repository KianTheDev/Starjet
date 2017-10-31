package me.thekian.cstmobs;

import net.minecraft.server.v1_11_R1.EntityVillager;
import net.minecraft.server.v1_11_R1.GenericAttributes;
import net.minecraft.server.v1_11_R1.PathfinderGoalAvoidTarget;
import net.minecraft.server.v1_11_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_11_R1.PathfinderGoalInteract;
import net.minecraft.server.v1_11_R1.PathfinderGoalInteractVillagers;
import net.minecraft.server.v1_11_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_11_R1.PathfinderGoalLookAtTradingPlayer;
import net.minecraft.server.v1_11_R1.PathfinderGoalMakeLove;
import net.minecraft.server.v1_11_R1.PathfinderGoalMoveIndoors;
import net.minecraft.server.v1_11_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_11_R1.PathfinderGoalOpenDoor;
import net.minecraft.server.v1_11_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_11_R1.PathfinderGoalRestrictOpenDoor;
import net.minecraft.server.v1_11_R1.PathfinderGoalTakeFlower;
import net.minecraft.server.v1_11_R1.PathfinderGoalTradeWithPlayer;

public class CustomEntityVillager extends EntityVillager
{
	
	public CustomEntityVillager(net.minecraft.server.v1_11_R1.World world) 
	{
		super(world);
	}

	@Override
	protected void initAttributes() 
	{
		super.initAttributes();
		getAttributeInstance(GenericAttributes.maxHealth).setValue(100D);
	}
	
	@Override
	protected void r()
	{
		goalSelector.a(0, new PathfinderGoalFloat(this));
		goalSelector.a(2, new PathfinderGoalRandomStroll(this, 0.59999999999999998D));
		goalSelector.a(4, new PathfinderGoalLookAtPlayer(this, net.minecraft.server.v1_11_R1.EntityInsentient.class, 8F));
	}

	@Override
	protected void o()
	{
		
	}
}