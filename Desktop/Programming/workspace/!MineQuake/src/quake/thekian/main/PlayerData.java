package quake.thekian.main;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import quake.thekian.weapons.Weapon;

public class PlayerData 
{
	private Player player;
	private Class<?> kit;
	private int cooldown, lastAbility, kills;
	private boolean alive, activeAbility, respawning;
	private double damageResistance, damageModifier;
	private Location startLocation;
	private Weapon[] weaponArray = {null, null, null, null, null, null, null, null, null};
	private int[] ammoArray = {0, 0, 0, 0, 0, 0, 0, 0, 0};

	public PlayerData(Player player, boolean isAlive)
	{
		this.player = player;
		this.kit = kit;
		this.activeAbility = false;
		this.cooldown = 0;
		this.damageResistance = 1;
		this.damageModifier = 1;
		this.alive = isAlive;
		this.lastAbility = 1;
		this.kills = 0;
	}
	
	public void setStartLocation(Location loc)
	{
		startLocation = loc;
	}
	
	public Location getStartLocation()
	{
		return startLocation;
	}
	
	public void teleportToStartLoc()
	{
		Location temp = startLocation;
		temp.setDirection(player.getLocation().getDirection());
		player.teleport(temp);
	}
	
	public void incrementCooldown()
	{
		if(cooldown <= 0)
		{
			cooldown = 0;
			return;
		}
		else
			cooldown--;
		if(cooldown <= 0)
		{
			cooldown = 0;
			try
			{
				player.sendMessage((String) kit.getClass().getMethod("Ability" + lastAbility + "Cooldown").invoke(null));
			} catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void addKill()
	{
		kills++;
	}
	
	public Weapon getWeapon(int i)
	{
		if(i < weaponArray.length && i > -1)
			return weaponArray[i];
		return null;
	}

	public void setWeapon(Weapon w, int i)
	{
		if(i < weaponArray.length && i > -1)
			weaponArray[i] = w;
	}
	
	public int getAmmo(int i)
	{
		if(i < ammoArray.length && i > -1)
			return ammoArray[i];
		return 0;
	}
	
	public void addAmmo(int val, int i)
	{
		if(i < ammoArray.length && i > -1)
			ammoArray[i] += val;
		else
			return;
		if(ammoArray[i] > 200)
			ammoArray[i] = 200;
		if(ammoArray[i] < 0)
			ammoArray[i] = 0;
	}
	
	public void setAmmo(int val, int i)
	{
		if(i < ammoArray.length && i > -1)
			ammoArray[i] = val;
		else
			return;
		if(ammoArray[i] > 200)
			ammoArray[i] = 200;
		if(ammoArray[i] < 0)
			ammoArray[i] = 0;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public Class<?> getKit()
	{
		return kit;
	}
	
	public int getCooldown()
	{
		return cooldown;
	}
	
	public boolean getAlive()
	{
		return alive;
	}
	
	public boolean getActive()
	{
		return activeAbility;
	}
	
	public double getDamageResistance()
	{
		return damageResistance;
	}
	
	public double getDamageModifier()
	{
		return damageModifier;
	}
	
	public int getLastAbility()
	{
		return lastAbility;
	}
	
	public int getKills()
	{
		return kills;
	}
	
	public void setCooldown(int i)
	{
		cooldown = i;
	}
	
	public void setAlive(boolean b)
	{
		alive = b;
	}
	
	public void setActive(boolean b)
	{
		activeAbility = b;
	}
	
	public void setDamageResistance(double d)
	{
		damageResistance = d;
	}
	
	public void setDamageModifier(double d)
	{
		damageModifier = d;
	}
	
	public void setKit(Class<?> k)
	{
		kit = k;
	}
	
	public void setLastAbility(int i)
	{
		lastAbility = i;
	}
	
	public void setRespawning(boolean b)
	{
		respawning = b;
	}
	
	public boolean getRespawning()
	{
		return respawning;
	}
}
