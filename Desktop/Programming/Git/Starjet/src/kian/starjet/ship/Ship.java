package kian.starjet.ship;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import kian.starjet.core.CoordSet;
import kian.starjet.core.Processor;
import kian.starjet.core.Team;

public class Ship 
{
	private double health, maxHealth, regen; //Current health, maximum health, and regeneration rate of the ship.
	private boolean respawning; //Whether the ship is currently respawning.
	private List<CoordSet> primaryFiringSpots, secondaryFiringSpots; //Relative locations to the ship where weapon fire spawns.
	private int fireOne, fireTwo; //Current index of the location from which weapons will fire.
	private int coolOne, coolTwo; //Cooldowns on the primary and secondary weapons, respectively.
	private AttackData primary, secondary; //Ship has a primary attack and (sometimes) a secondary attack.
	private double maxSpeed, acceleration; //Maximum speed and acceleration.
	private double[] dimensions; //Approximate dimensions of the ship for hitbox detection.
	//{x max, x min, y max, y min, z max, z min}
	private ArmorStand shipModel; //Armor stand carrying ship. Player is set as passenger of this.
	private Material shipMat; private short data; //Ship appearance
	private Team team; //For targeting info
	private Ship damager; //Last enemy to hit the ship
	private Vector vector;
	
	public Ship(double health, double regen, List<CoordSet> firingOne, List<CoordSet> firingTwo, AttackData primary, AttackData secondary, double maxSpeed, double acceleration, Material mat, short data, Team team)
	{
		fireOne = 0;
		fireTwo = 0;
		this.health = health;
		this.maxHealth = health;
		this.regen = regen;
		primaryFiringSpots = firingOne;
		secondaryFiringSpots = firingTwo;
		this.primary = primary;
		this.secondary = secondary;
		this.maxSpeed = maxSpeed;
		this.acceleration = acceleration;
		this.shipMat = mat;
		this.data = data;
		this.team = team;
		damager = null;
		respawning = false;
		dimensions = new double[]{2, -2, 1.5, -1.5, 2, -2};
	}
	
	/***
	 * Does some trig to get the location of the firing location for the primary weapon
	 * @return Angle-corrected location
	 */
	public Location getPrimaryFiringLoc()
	{
		if(!(primaryFiringSpots != null && primaryFiringSpots.size() > 0))
			return null;
		CoordSet cs = primaryFiringSpots.get(fireOne);
		fireOne++;
		if(fireOne >= primaryFiringSpots.size())
			fireOne = 0;
		Location loc = shipModel.getLocation();
		Vector velocity = shipModel.getVelocity();
		double angle1 = Math.atan(velocity.getZ() / velocity.getX()); //Angle in X direction from YZ plane
		double angle2 = Math.atan(velocity.getY() / Math.sqrt(Math.pow(velocity.getX(), 2) + Math.pow(velocity.getZ(), 2))); //Angle in Y direction from XZ plane
		double angle3 = Math.atan(velocity.getX() / velocity.getZ()); //Angle in Z direction from XY plane
		return new Location(shipModel.getWorld(), 
				loc.getX() + Math.sin(angle1) * cs.getX(), 
				loc.getY() + Math.sin(angle2) * cs.getY(), 
				loc.getZ() + Math.sin(angle3) * cs.getZ());
	}
	
	/***
	 * Fires the ship's primary weapon.
	 */
	public void attackPrimary()
	{
		if(primary == null || coolOne > 0)
			return;
		Location loc = getPrimaryFiringLoc();
		Processor.addAttack(loc != null ? loc : getLocation(), primary, this);
		coolOne = primary.getCooldown();
	}
	
	/***
	 * Fires the ship's secondary weapon.
	 */
	public void attackSecondary()
	{
		if(secondary == null || coolTwo > 0)
			return;
		Location loc = getSecondaryFiringLoc();
		Processor.addAttack(loc != null ? loc : getLocation(), secondary, this);
		coolTwo = secondary.getCooldown();
	}
	
	/***
	 * Does some trig to get the location of the firing location for the secondary weapon
	 * @return Angle-corrected location
	 */
	public Location getSecondaryFiringLoc()
	{
		if(!(secondaryFiringSpots != null && secondaryFiringSpots.size() > 0))
			return null;
		CoordSet cs = secondaryFiringSpots.get(fireTwo);
		fireTwo++;
		if(fireTwo >= secondaryFiringSpots.size())
			fireTwo = 0;
		Location loc = shipModel.getLocation();
		Vector velocity = shipModel.getVelocity();
		double angle1 = Math.atan(velocity.getZ() / velocity.getX()); //Angle in X direction from YZ plane
		double angle2 = Math.atan(velocity.getY() / Math.sqrt(Math.pow(velocity.getX(), 2) + Math.pow(velocity.getZ(), 2))); //Angle in Y direction from XZ plane
		double angle3 = Math.atan(velocity.getX() / velocity.getZ()); //Angle in Z direction from XY plane
		return new Location(shipModel.getWorld(), 
				loc.getX() + Math.sin(angle1) * cs.getX(), 
				loc.getY() + Math.sin(angle2) * cs.getY(), 
				loc.getZ() + Math.cos(angle3) * cs.getZ());
	}
	
	public ArmorStand getShipModel()
	{
		return shipModel;
	}
	
	/***
	 * Creates armor stand with ship model. Also performs respawning operations.
	 * @param loc - Location to spawn the armor stand.
	 * @return ArmorStand with appropriate data
	 */
	public ArmorStand generateShipModel(Location loc)
	{
		//Set as respawned
		respawning = false;
		health = maxHealth;
		coolOne = 0;
		coolTwo = 0;
		
		ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		as.setGravity(true);
		as.setVisible(false);
		as.setInvulnerable(true);
		as.setSmall(true);
		//as.setCollidable(false);
		as.setSilent(true);
		
		//Item
		ItemStack is = new ItemStack(shipMat);
		is.setDurability(data);
		as.setHelmet(is);
		
		//Add ship to valid targets in processor
		Processor.getTargets().add(this);
		
		shipModel = as;
		return shipModel;
	}
	
	/***
	 * Returns the approximate location of the center of the ship.
	 * @return Center of ship if shipModel not null, null otherwise
	 */
	public Location getLocation()
	{
		return shipModel == null ? null : shipModel.getEyeLocation();
	}
	
	public World getWorld()
	{
		return shipModel.getWorld();
	}
	
	public double getHealth()
	{
		return health;
	}
	
	public void setHealth(double d)
	{
		health = d;
	}
	
	/***
	 * Damages the ship.
	 * @param d - Amount of damage dealt.
	 */
	public void damage(double d)
	{
		health -= (d > health ? health : d);
	}
	
	/***
	 * Damages the ship and records the attacker.
	 * @param d - Amount of damage dealt.
	 * @param damager - Ship responsible for damage.
	 */
	public void damage(double d, Ship damager)
	{
		health -= (d > health ? health : d);
		this.damager = damager;
	}
	
	public void regen()
	{
		if(health == maxHealth)
			return;
		health += regen;
		if(health > maxHealth)
			health = maxHealth;
	}
	
	public void cooldown()
	{
		if(coolOne > 0)
			coolOne--;
		if(coolTwo > 0)
			coolTwo--;
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public void setTeam(Team t)
	{
		team = t;
	}
	
	public AttackData getPrimaryWeapon()
	{
		return primary;
	}
	
	public AttackData getSecondaryWeapon()
	{
		return secondary;
	}
	
	public double getMaxSpeed()
	{
		return maxSpeed;
	}
	
	public double getAcceleration()
	{
		return acceleration;
	}
	
	//These two methods are for the benefit of the turret descendent class.
	protected List<CoordSet> getPrimaryFiringLocs()
	{
		return primaryFiringSpots;
	}
	
	protected List<CoordSet> getSecondaryFiringLocs()
	{
		return secondaryFiringSpots;
	}
	
	/***
	 * Returns array with dimensions of the ship around its center. Indices and meaning are, respectively,
	 * 0: +X, 1: -X, 2: +Y, 3: -Y, 4: +Z, 5: -Z 
	 * @return Array containing dimensions of the ship
	 */
	public double[] getDimensions()
	{
		return dimensions;
	}
	
	public void setDimensions(double[] darr)
	{
		dimensions = darr;
	}
	
	public Ship getDamager()
	{
		return damager;
	}
	
	/***
	 * Kills and resets the ship.
	 * @return Last damager if one exists, null otherwise.
	 */
	public Ship killShip()
	{
		Processor.removeTarget(this);
		respawning = true;
		shipModel.remove();
		health = maxHealth;
		coolOne = 0;
		fireOne = 0;
		coolTwo = 0;
		fireTwo = 0;
		Ship temp = damager;
		damager = null;
		return temp;
	}
	
	public boolean getRespawning()
	{
		return respawning;
	}
	
	public Material getMat()
	{
		return shipMat;
	}
	
	public short getData()
	{
		return data;
	}
	
	public int getCoolOne()
	{
		return coolOne;
	}
	
	public int getCoolTwo()
	{
		return coolTwo;
	}
	
	public enum ShipType
	{
		VIPER, STARJET, MARAUDER;
	}
	
	public void setVelocity(Vector v)
	{
		vector = v;
	}
	
	public Vector getVelocity()
	{
		return vector;
	}
	
	protected void setCoolOne(int i)
	{
		coolOne = i;
	}
	
	protected void setCoolTwo(int i)
	{
		coolTwo = i;
	}
	
	public boolean isAlive()
	{
		return !(health <= 0 || respawning || shipModel == null);
	}
}