package quake.thekian.weapons;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import quake.thekian.weapons.AttackData.ArmorStandData;

public class ArmorStandProjectile 
{
	private ArmorStand entityProjectile;
	private ArmorStandData attackData;
	private Vector velocity;
	private int maxAge, age;
	private Player owner;
	private boolean dead;
	
	public ArmorStandProjectile(Location loc, ArmorStandData attackData, Vector velocity, int maxAge, Player owner)
	{
		dead = false;
		entityProjectile = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		entityProjectile.getLocation().setDirection(velocity.normalize());
		entityProjectile.setVisible(false);
		entityProjectile.setSmall(true);
		entityProjectile.setCollidable(false);
		entityProjectile.setGravity(attackData.getGravity());
		entityProjectile.setInvulnerable(true);
		if(attackData.getOnHead())
			entityProjectile.setHelmet(attackData.getItem());
		else
		{
			entityProjectile.setItemInHand(attackData.getItem());
			entityProjectile.setRightArmPose(new EulerAngle(0, 210, 0));
		}
		this.attackData = attackData;
		this.velocity = velocity;
		this.maxAge = maxAge;
		this.age = 0;
		this.owner = owner;
	}
	
	public ArmorStand getProjectile()
	{
		return entityProjectile;
	}
	
	public Vector getVelocity()
	{
		return velocity;
	}
	
	public ArmorStandData getAttackData()
	{
		return attackData;
	}
	
	public boolean incrementAge()
	{
		age++;
		if(age < maxAge)
			return false;
		else
		{
			entityProjectile.remove();
			return true;
		}
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public void setVelocity(Vector v)
	{
		velocity = v;
	}
	
	public void kill()
	{
		entityProjectile.remove();
		dead = true;
	}
	
	public boolean getDead()
	{
		return dead;
	}
}
