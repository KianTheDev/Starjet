package kian.starjet.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import kian.starjet.ship.AttackData;
import kian.starjet.ship.AttackType;
import kian.starjet.ship.Ship;
import kian.starjet.ship.Ship.ShipType;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

public class PlayerData
{
	private Ship ship; //Player's ship
	private CoordSet startLoc; //Spawn location for player
	private Player player; //Technically, the player data is stored in a hashmap where a player's UUID is the key. However, in some places, accessing the player from the PlayerData instance itself is preferable.
	private int kills;
	private Team team;
	private Vector currentDirection; //Used to check if the player's current vector is the same as it was before; if so, it skips recalculating the relatively math-intensive angles.
	
	public PlayerData(CoordSet startLoc, Team team, Player player)
	{
		this.startLoc = startLoc;
		kills = 0;
		this.team = team;
		this.player = player;
	}
	
	public Ship getShip()
	{
		return ship;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public CoordSet getStartLoc()
	{
		return startLoc;
	}
	
	public int getKills()
	{
		return kills;
	}
	
	public void addKills(int i)
	{
		kills += i;
	}
	
	public void setKills(int i)
	{
		kills = i;
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public Vector getCurrentDirection()
	{
		return currentDirection;
	}
	
	public void setCurrentDirection(Vector v)
	{
		currentDirection = v;
	}
	
	public void setShip(ShipType type)
	{
		if(type.equals(ShipType.STARJET))
		{
			ship = new Ship(200, 1, /*List<CoordSet> firingOne, List<CoordSet> firingTwo,*/ Arrays.asList(new CoordSet[]{new CoordSet(2, 0, 0), new CoordSet(-2, 0, 0)}), Arrays.asList(new CoordSet[]{new CoordSet(0, 0, 0)}),
					new AttackData(AttackType.PROJECTILE, 4, 35, 0.1, 5, ParticleTypeEnum.CRIT_MAGIC, null, null, (byte) 0, 1),
					new AttackData(AttackType.MODEL, 75, 50, 0.05, 8, null, null, Material.FIREBALL, (byte) 0, 100),
					6,6, Material.INK_SACK, (short) 6, team);
		} else if(type.equals(ShipType.VIPER))
		{
			ship = new Ship(350, 2, /*List<CoordSet> firingOne, List<CoordSet> firingTwo,*/ Arrays.asList(new CoordSet[]{new CoordSet(2, 0, 0), new CoordSet(-2, 0, 0)}), Arrays.asList(new CoordSet[]{new CoordSet(0, 0, 0)}),
					new AttackData(AttackType.PROJECTILE, 25, 45, 0.05, 7.5, ParticleTypeEnum.VILLAGER_HAPPY, null, null, (byte) 0, 5),
					null, 4, 4, Material.INK_SACK, (short) 5, team);
		} else if(type.equals(ShipType.MARAUDER))
		{
			ship = new Ship(500, 3, /*List<CoordSet> firingOne, List<CoordSet> firingTwo,*/ Arrays.asList(new CoordSet[]{new CoordSet(2, 0, 0), new CoordSet(-2, 0, 0)}), Arrays.asList(new CoordSet[]{new CoordSet(0, 0, 0)}),
					new AttackData(AttackType.BEAM, 1, 20, 0.05, 5, ParticleTypeEnum.REDSTONE, null, null, (byte) 0, 1),
					new AttackData(AttackType.MODEL, 100, 50, 0.05, 5, null, null, Material.ANVIL, (byte) 0, 25),
					2, 2, Material.INK_SACK, (short) 4, team);
		}
	}
	
}
