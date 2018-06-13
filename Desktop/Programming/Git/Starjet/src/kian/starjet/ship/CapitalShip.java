package kian.starjet.ship;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import kian.starjet.core.CoordSet;
import kian.starjet.core.Team;
import kian.starjet.ship.Turret.TurretPresets;
import kian.starjet.util.BlockData;
import kian.starjet.util.Schematic;

/***
 * Holds data on the massive ship used in the Defend and Destroy gamemode.
 */
public class CapitalShip
{
	private Schematic structure; //Block structure of the ship
	private int[] dimensions; //Hitbox of the ship proper
	private List<Turret> turrets = new ArrayList<Turret>(); //Ship's turrets
	private Location loc; //Ship's location
	private Team team;
	private int health, maxHealth;
	private Ship damager;
	private boolean alive;
	private int value;
	private HashMap<CoordSet, Turret> turretDataStorage = new HashMap<CoordSet, Turret>();
	
	/**
	 * Loads the capital ship from a data file.
	 * @param f - File from which to load.
	 */
	public CapitalShip(File f)
	{ 
		alive = true;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));
			String s = "";
			while((s = reader.readLine()) != null)
			{
				if(s.startsWith("#") || s.startsWith("//"))
					continue;
				else if(s.startsWith("health="))
				{
					maxHealth = Integer.valueOf(s.substring(7));
					health = maxHealth;
				} else if(s.startsWith("dims=")) //Dimensions
				{
					dimensions = new int[6];
					int b = 0, c = 5;
					for(int i = 5; i < s.length(); i++)
					{
						if((s.charAt(i) == ',' || i == s.length() - 1) && b < 6)
						{
							dimensions[b] = Integer.valueOf(s.substring(c, i));
							b++;
							c = i + 1;
						}
					}
				} else if(s.startsWith("value="))
				{
					value = Integer.valueOf(s.substring(6));
				} else if(s.startsWith("turret="))
				{//World .zip file
					int b = 0, c = 7;
					CoordSet cs = new CoordSet(0, 0, 0);
					String type = "";
					for(int i = 7; i < s.length(); i++)
					{
						if(s.charAt(i) == ',' || i == s.length() - 1)
							switch(b)
							{
							case 0:
								cs.setX(Double.valueOf(s.substring(c, i)));
								c = i + 1;
								b++;
								break;
							case 1:
								cs.setY(Double.valueOf(s.substring(c, i)));
								c = i + 1;
								b++;
								break;
							case 2:
								cs.setZ(Double.valueOf(s.substring(c, i)));
								c = i + 1;
								b++;
								break;
							case 3:
								type = s.substring(c);
								b++;
								break;
							}
					}
					Turret t = TurretPresets.valueOf(type).getTurret(); //Gets turret data from the enum in Turret.class
					turretDataStorage.put(cs, t);
				}
			}
			reader.close();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/***
	 * Generates or regenerates the ship's turrets from its existing loaded data. If none exists, this won't do much.
	 */
	public void generateTurrets()
	{
		if(turrets == null || turretDataStorage == null)
			return;
		for(Turret t : turrets)
			t.killShip();
		turrets.clear();
		for(CoordSet cs : turretDataStorage.keySet())
		{
			Turret t = turretDataStorage.get(cs).clone();
			turrets.add(t);
			t.generateShipModel(new Location(loc.getWorld(), loc.getX() + cs.getX(), loc.getY() + cs.getY(), loc.getZ() + cs.getZ()));
		}
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public int getMaxHealth()
	{
		return maxHealth;
	}
	
	public void setHealth(int i)
	{
		health = i;
	}
	
	public void damage(double d)
	{
		health -= (int) d;
		if(health < 0)
			health = 0;
	}
	
	/***
	 * Damages the ship and stores its damager.
	 * @param i - Damage to be dealt.
	 * @param damager - Ship responsible for damage.
	 */
	public void damage(double d, Ship damager)
	{
		damage(d);
		this.damager = damager;
	}
	
	public void repair(int i)
	{
		health += i;
		if(health > maxHealth)
			health = maxHealth;
	}
	
	public Location getLocation()
	{
		return loc;
	}
	
	public Schematic getStructure()
	{
		return structure;
	}
	
	public void loadSchematic(File f, int i)
	{
		structure = new Schematic(f);
		for(BlockData bd : structure.getStructure().values())
		{
			if(bd.getMaterial().equals(Material.CONCRETE) || bd.getMaterial().equals(Material.WOOL) || bd.getMaterial().equals(Material.STAINED_CLAY))
				bd.setData(i);
		}
	}
	
	public void setSchematic(Schematic s, int i)
	{
		structure = s;
		for(BlockData bd : structure.getStructure().values())
		{
			if(bd.getMaterial().equals(Material.CONCRETE) || bd.getMaterial().equals(Material.WOOL) || bd.getMaterial().equals(Material.STAINED_CLAY))
				bd.setData(i);
		}
	}
	
	/***
	 * Builds the capital ship at a given location.
	 * @param loc - Location at which to place the ship.
	 */
	public void build(Location loc)
	{
		HashMap<CoordSet, BlockData> schem = structure.getStructure();
		for(CoordSet cs : schem.keySet())
		{
			Block b = loc.getWorld().getBlockAt(loc.getBlockX() + (int) cs.getX(), loc.getBlockY() + (int) cs.getY(), loc.getBlockZ() + (int) cs.getZ());
			b.setType(schem.get(cs).getMaterial()); //Set block type
			b.setData((byte) schem.get(cs).getData()); //Set material data
		}
		this.loc = loc;
	}
	
	public void unbuild()
	{
		for(Turret t : turrets)
			if(!t.getRespawning()) //If it's not already dead...
				t.killShip();
		turrets.clear();
		for(CoordSet cs : structure.getStructure().keySet())
		{
			Block b = loc.getWorld().getBlockAt(loc.getBlockX() + (int) cs.getX(), loc.getBlockY() + (int) cs.getY(), loc.getBlockZ() + (int) cs.getZ());
			b.setType(Material.AIR); //Set to air
		}
	}
	
	public void killShip()
	{
		//Kill, unbuild, give points
		alive = false;
		unbuild();
		if(damager != null)
			damager.getTeam().addPoints(value);
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public List<Turret> getTurrets()
	{
		return turrets;
	}

	public int[] getDimensions()
	{
		return dimensions;
	}
	
	public void setDamager(Ship s)
	{
		damager = s;
	}
	
	public Ship getDamager()
	{
		return damager;
	}
	
	public boolean isAlive()
	{
		return alive;
	}
	
	public void setTeam(Team t)
	{
		team = t;
		for(Turret tur : turrets)
			tur.setTeam(t);
	}
	
}
