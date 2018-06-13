package kian.starjet.ship;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * The class which holds data for each player's attacks. Ordinary projectiles are not used, but aside from custom particle
 * attacks, 3D modeled attacks using a resource pack and armor stands are also used. 
 */
public class AttackData
{
	private AttackType type; //Type of attack this weapon uses
	private double damage, range, size, speed; //General information about weapon function
	private int cooldown;
	private ParticleTypeEnum data1, data2; //Data 1 is for all particle attacks, data 2 is for a spell's extra particle effect.
	private Material data3; //Data 3 is the material used for a 3D model-based attack
	private short data4; //Data 4 is the durability on the item created for AttackType.MODEL
	
	public AttackData(AttackType type, double damage, double range, double size, double speed, ParticleTypeEnum data1, ParticleTypeEnum data2, Material data3, byte data4, int cooldown)
	{
		this.type = type;
		this.damage = damage;
		this.range = range;
		this.size = size;
		this.speed = speed;
		this.data1 = data1;
		this.data2 = data2;
		this.data3 = data3;
		this.data4 = data4;	
		this.cooldown = cooldown;
	}
	
	public AttackType getAttackType()
	{
		return type;
	}
	
	public double getDamage()
	{
		return damage;
	}
	
	public double getRange()
	{
		return range;
	}
	
	public double getSize()
	{
		return size;
	}
	
	public double getSpeed()
	{
		return speed;
	}
	
	public int getCooldown()
	{
		return cooldown;
	}
	
	public ParticleTypeEnum getData1()
	{
		return data1;
	}
	
	public ParticleTypeEnum getData2()
	{
		return data2;
	}
	
	public Material getData3()
	{
		return data3;
	}	
	
	public short getData4()
	{
		return data4;
	}
	
	/***
	 * Creates an armor stand with the proper attack-specific 3D modeled item on its head.
	 * @param loc - Location to spawn the armor stand
	 * @return ArmorStand w/ appropriate data
	 */
	public ArmorStand generate3DModel(Location loc)
	{
		if(data3 == null || data4 < 0)
			return null;
		
		//Creates invisible, small, non-gravity-affected, non-collidable and invulnerable armor stand carrying the desired model on its head
		ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		as.setSmall(true);
		as.setGravity(false);
		as.setVisible(false);
		as.setCollidable(false);
		as.setInvulnerable(true);
		ItemStack is = new ItemStack(data3);
		is.setDurability(data4);
		as.setHelmet(is);
		
		return as;
	}
}
