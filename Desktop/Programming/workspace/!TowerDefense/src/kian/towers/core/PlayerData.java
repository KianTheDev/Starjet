package kian.towers.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

import kian.towers.tower.Tower;

public class PlayerData
{
	private int essence; //Game currency. Obtained by killing enemies.
	private List<Tower> towers; //Towers owned by the player.
	private List<LivingEntity> ownedMobs; //Mobs spawned along the player's path. Money from kills go to that player, regardless of who actually killed it
	private List<CoordSet> nodes; //Node locations. Mobs will move to the location of the node. When they reach the last one, damage is dealt.
	private int lives; //Lives remaining to the player. Begin to be lost if enemies reach the center.
	private CoordSet startLoc; //Spawn location for player
	private CoordSet gate; //Location from which hostile mobs come.
	private int data; //Color of hardened clay (AKA terracotta) to use for tower placement
	private int selection; //For tower selection
	
	public PlayerData(CoordSet startLoc, CoordSet gate, int data)
	{
		this.startLoc = startLoc;
		this.gate = gate;
		this.data = data;
		lives = 100;
		essence = 50000;
		towers = new ArrayList<Tower>();
		ownedMobs = new ArrayList<LivingEntity>();
		nodes = new ArrayList<CoordSet>();
		selection = 0;
	}
	
	public int getMoney()
	{
		return essence;
	}
	
	public void setMoney(int i)
	{
		essence = i;
	}
	
	public void addMoney(int i)
	{
		essence += i;
	}
	
	public List<Tower> getTowers()
	{
		return towers;
	}
	
	public void addTower(Tower t)
	{
		towers.add(t);
	}
	
	public List<LivingEntity> getPlayerMobs()
	{
		return ownedMobs;
	}
	
	public List<CoordSet> getNodes()
	{
		return nodes;
	}
	
	public void setNodes(List<CoordSet> l)
	{
		nodes = l;
	}
	
	
	public int getLives()
	{
		return lives;
	}
	
	public void damage(int i)
	{
		lives -= i;
		if(lives < 0)
			lives = 0;
	}
	
	public CoordSet getStartLoc()
	{
		return startLoc;
	}
	
	public CoordSet getMobSpawn()
	{
		return gate;
	}
	
	//Block color
	public int getData()
	{
		return data;
	}
	
	public int getSelection()
	{
		return selection;
	}
	
	public void setSelection(int i)
	{
		selection = i;
	}
	
	//Sets selection to a given tower
	public void setSelection(Tower t)
	{
		selection = towers.indexOf(t);
	}
	
	public int calculateNetAssets()
	{
		int i = essence;
		for(Tower t : towers)
		{
			i += t.getValue();
		}
		return i;
	}
}
