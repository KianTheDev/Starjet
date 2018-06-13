package kian.starjet.attacks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import kian.starjet.ship.Ship;

/***
 * 3D modelled attacks, using items on top of armor stands.
 */
public class ModelAttack
{

	private double damage;
	private Material mat;
	private short data;
	private ArmorStand model;
	private double speed, size;
	private Vector direction;
	private Ship owner;
	private int maxAge, age;
	
	public ModelAttack(Location loc, double damage, Material mat, short data, double speed, double size, Vector direction, Ship owner)
	{
		ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		as.setInvulnerable(true);
		as.setVisible(false);
		((org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand) as).getHandle().setNoGravity(true);
		as.setCollidable(false);
		as.setSmall(true);
		as.setSilent(true);
		
		ItemStack is = new ItemStack(mat);
		is.setDurability(data);
		as.setHelmet(is);
		
		model = as;
		
		this.mat = mat;
		this.data = data;
		this.damage = damage;
		this.speed = speed;
		this.size = size;
		this.direction = direction;
		this.owner = owner;
		maxAge = 600;
		age = 0;
	}
	
	public void updateMovement()
	{
		//Allows it to pass through blocks to hit the collision box of capital ships
		model.teleport(model.getLocation().clone().add(direction.clone().multiply(speed / 20)));
		//model.setVelocity(direction.clone().multiply(speed));
	}
	
	public double getDamage()
	{
		return damage;
	}
	
	public Vector getDirection()
	{
		return direction;
	}
	
	public ArmorStand getModel()
	{
		return model;
	}
	
	public Location getLocation()
	{
		return model.getEyeLocation();
	}
	
	public double getSpeed()
	{
		return speed;
	}
	
	public double getSize()
	{
		return size;
	}
	
	public Material getMaterial()
	{
		return mat;
	}
	
	public short getData()
	{
		return data;
	}
	
	public Ship getOwner()
	{
		return owner;
	}

	public int getMaxAge()
	{
		return maxAge;
	}
	
	public int getAge()
	{
		return age;
	}
	
	public void setAge(int i)
	{
		age = i;
	}
	
	public void incrementAge()
	{
		age++;
	}
}
