package kian.towers.tower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import kian.towers.core.CoordSet;
import kian.towers.core.Processor;

/***
 * Superclass of different tower types to be implemented. Includes abstract methods to register upgrades.
 * If you're attempting to instantiate this, you're doing something wrong.
 * @author TheKian
 *
 */
public abstract class Tower
{
	private Schematic schem; //Holds the tower's build information
	private AttackData attack; //Holds information on the tower's attack.
	private Location loc; //World location
	private String[] upgradeNames, upgradeDescs; //Contains tower and upgrade names and descriptions.
	private int path1L, path2L; //Level of upgrade paths. Only one third level upgrade may be selected.
	private int cooldown; //Must be 0 to attack enemy.
	private double multiplier; //Multiplier on the cost of the tower and its upgrades.
	private Player owner; //Player who owns tower.
	private TargetEnum targetType; //Type of targeting used

	public Tower(Schematic schem, AttackData attack, Player owner)
	{
		this.schem = schem;
		this.attack = attack;
		this.owner = owner;
		path1L = 0;
		path2L = 0;
		targetType = TargetEnum.FIRST;
	}
	
	public Tower(Schematic schem, AttackData attack, Location loc, Player owner)
	{
		this(schem, attack, owner);
		this.loc = loc;
	}
	
	//Each tower implements the upgrade method differently, allowing for easy calls to upgrade individual towers
	public abstract void upgradeP1L1();
	public abstract void upgradeP1L2();
	public abstract void upgradeP1L3();
	public abstract void upgradeP2L1();
	public abstract void upgradeP2L2();
	public abstract void upgradeP2L3();
	
	/***
	 * Processes attacking.
	 * @param targets
	 * @param plugin
	 * @return
	 */
	public boolean attack(List<LivingEntity> targets, Plugin plugin)
	{
		if(cooldown > 0) //If the tower is unable to fire, returns false
			return false;//Check must always be made when method is called, avoids excessive getter methods
		List<LivingEntity> newTargets = new ArrayList<LivingEntity>();
		for(LivingEntity le : targets) //Reduces list of potential targets to those in range
		{
			double range = Math.sqrt(Math.pow(loc.getX() - le.getLocation().getX(), 2) + Math.pow(loc.getX() - le.getLocation().getX(), 2));
			if(range <= attack.getRange())
				newTargets.add(le);
		}
		if(newTargets.size() == 0)
			return false;
		double rngtmp = -1;
		int id = -1;
		//TODO
		//OPTIMIZE THIS MORE

		//Different types of targeting based on the selected option
		if(targetType.equals(TargetEnum.CLOSEST))
		{
			//Targets closest enemy
			for(int i = 0; i < newTargets.size(); i++) //Not using a for-each loop to avoid index issues
			{
				LivingEntity le = newTargets.get(i);
				double range = Math.sqrt(Math.pow(loc.getX() - le.getLocation().getX(), 2) + Math.pow(loc.getZ() - le.getLocation().getZ(), 2)); 
				if(range <= attack.getRange() && (range < rngtmp || rngtmp < 0))
				{
					id = i;
					rngtmp = range;
				}
			}
		} else if(targetType.equals(TargetEnum.STRONGEST))
		{
			//Targets strongest enemy in range
			for(int i = 0; i < targets.size(); i++) //Not using a for-each loop to avoid index issues
			{
				LivingEntity le = targets.get(i);
				double range = Math.sqrt(Math.pow(loc.getX() - le.getLocation().getX(), 2) + Math.pow(loc.getZ() - le.getLocation().getZ(), 2)); 
				if(range <= attack.getRange() && le.getMaxHealth() > rngtmp)
				{
					id = i;
					rngtmp = le.getMaxHealth(); //Uses rngtmp to represent HP
					break;
				}
			}
		} else
		{
			//Targets first enemy in range
			for(int i = 0; i < targets.size(); i++) //Not using a for-each loop to avoid index issues
			{
				LivingEntity le = targets.get(i);
				double range = Math.sqrt(Math.pow(loc.getX() - le.getLocation().getX(), 2) + Math.pow(loc.getZ() - le.getLocation().getZ(), 2)); 
				if(range <= attack.getRange())
				{
					id = i;
					break;
				}
			}
		}

		if(id == -1) //If no target is acquired, return false
			return false;
		cooldown = attack.getSpeed(); //If an attack is launched, activate cooldown.

		Location loc2 = loc; //If the attack is not an AOE field, the attack must not collide with the tower
		if(!attack.getAttackType().equals(AttackType.AOE_FIELD))
			loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY() + schem.getDimensions()[1] + 1, loc.getZ());

		Processor.addAttack(loc2, attack, owner, newTargets, id); //Processor takes care of assembling the attack data
		return true; //Attack is scheduled, return true
	}

	/***
	 * Constructs a tower at the given location. Also changes the tower's internal location. 
	 * @param loc - Center of the tower to be constructed. Sets tower location.
	 */
	public void buildTower(Location loc)
	{
		HashMap<CoordSet, BlockData> structure = schem.getStructure();
		for(CoordSet cs : structure.keySet())
		{
			Block b = loc.getWorld().getBlockAt(loc.getBlockX() + (int) cs.getX(), loc.getBlockY() + (int) cs.getY(), loc.getBlockZ() + (int) cs.getZ());
			b.setType(structure.get(cs).mat); //Set block type
			b.setData((byte) structure.get(cs).dat); //Set material data
		}
		this.loc = loc;
	}
	
	public void buildTower()
	{
		buildTower(loc);
	}
	
	/***
	 * Destroys the tower once it has been constructed. Uses internally stored location set in buildTower().
	 */
	public void destroyTower()
	{
		HashMap<CoordSet, BlockData> structure = schem.getStructure(); //Uses altered buildTower() method
		for(CoordSet cs : structure.keySet())
		{
			loc.getWorld().getBlockAt(loc.getBlockX() + (int) cs.getX(), loc.getBlockY() + (int) cs.getY(), loc.getBlockZ() + (int) cs.getZ()).setType(Material.AIR); //Set block to air
		}
	}
	
	//Primary use is tower hitboxes
	public Schematic getStructure()
	{
		return schem;
	}
	
	//Generic setter method; shouldn't be used in-game, only for global towers in main class.
	public void setStructure(Schematic s)
	{
		schem = s;
	}
	
	//Basic getter method
	public AttackData getAttackData()
	{
		return attack;
	}
	
	//Used to check selection permissions.
	public Player getOwner()
	{
		return owner;
	}
	
	//Used to determine costs
	public double getMultiplier()
	{
		return multiplier;
	}
	
	public void setMultiplier(double d)
	{
		multiplier = d;
	}
	
	/***
	 * Returns the description of tower upgrades.
	 * @param i - Index of the description to be fetched. 0 for tower description, 1-3 for upgrade path 1, 4-6 for upgrade path 2.
	 * @return
	 */
	public String getDescription(int i)
	{
		return upgradeDescs[Math.min(i, 6)];
	}
	
	/***
	 * Returns the description of tower upgrades.
	 * @param i - Index of the description to be fetched. 0 for tower name, 1-3 for upgrade path 1, 4-6 for upgrade path 2.
	 * @return
	 */
	public String getName(int i)
	{
		return upgradeNames[Math.min(i, 6)];
	}
	
	/***
	 * Increments weapon cooldown by two server ticks.
	 */
	public void incrementCooldown()
	{
		if(cooldown > 0)
			cooldown--;
	}
	
	
	public void upgradeTower(int i)
	{
		if(i == 1)
		{
			if(path1L < 3)
			{
				path1L++;
				switch(path1L)
				{
					case 1:
						upgradeP1L1();
						break;
					case 2:
						upgradeP1L2();
						break;
					case 3:
						upgradeP1L3();
						break;
					default:
						break;
				}
			}
		} else if(i == 2)
		{
			if(path2L < 3)
			{
				path2L++;
				switch(path2L)
				{
					case 1:
						upgradeP2L1();
						break;
					case 2:
						upgradeP2L2();
						break;
					case 3:
						upgradeP2L3();
						break;
					default:
						break;
				}
			} else
				Bukkit.getLogger().warning("Invalid value was passed to upgradeTower() - i = " + i); 
		}
	}
	
	/***
	 * Sets the level of an upgrade path. Mostly for tower implementation utility.
	 * @param i - Path to change. Valid values: 1 and 2.
	 * @param i2 - Level to set. Valid values: 1 to 3.
	 */
	public void setUpgradeLevel(int i, int i2)
	{
		switch(i)
		{
			case 1:
				path1L = i2;
				break;
			case 2:
				path2L = i2;
				break;
			default:
				return;
		}
	}
	
	/***
	 * Returns the level of an upgrade path.
	 * @param path - Path to return. Valid values: 1 and 2
	 * @return Integer value of path progression
	 */
	public int getUpgradeLevel(int path)
	{
		switch(path)
		{
			case 1:
				return path1L;
			case 2:
				return path2L;
			default:
				return -1;
		}
	}
	
	/***
	 * Sets the value of the tower and upgrade names.
	 * @param upgradeNames - Array to pass to the method.
	 */
	public void setUpgradeNames(String[] upgradeNames)
	{
		this.upgradeNames = upgradeNames;
	}
	
	/***
	 * Sets the value of the tower and upgrade descriptions.
	 * @param upgradeDescs - Array to pass to the method.
	 */
	public void setUpgradeDescs(String[] upgradeDescs)
	{
		this.upgradeDescs = upgradeDescs;
	}
	
	public Location getLocation()
	{
		return loc;
	}
	
	public void setTargetType(TargetEnum t)
	{
		targetType = t;
	}
	
	public TargetEnum getTargetType()
	{
		return targetType;
	}
	
	public enum TargetEnum
	{
		CLOSEST("Closest"), STRONGEST("Strongest"), FIRST("First");

		private String value;	
		private TargetEnum(String value)
		{
			this.value = value;
		}
		
		public String getValue()
		{
			return value;
		}
	}
	
	public int getValue()
	{
		int i = (int) (multiplier * 250);
		for(int i2 = 3; i2 > 0; i2--)
		{
			if(path1L >= i)
				i += (int) (25 * Math.pow(2, i + 1) * multiplier);
			if(path2L >= i)
				i += (int) (25 * Math.pow(2, i + 1) * multiplier);
		}
		return i;
	}
	
}