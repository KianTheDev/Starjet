package kian.towers.enemy;

import org.bukkit.entity.LivingEntity;

public class MobData
{
	private int health; //Starting health of this kind of mob.
	private int level; //Armored, flying, or boss. Determines how weapons harm the mob. See AttackData 'damageLevel' for more details.
	private int damage; //Number of lives the mob takes away if it makes it through.
	private int essence; //Amount of currency the mob gives on its death.
	private int node; //Pathfinding value. Current node that the mob is following.
	private float walkspeed; //Mob's speed.
	
	public MobData(int health, int level, int damage, int essence, float walkspeed)
	{
		this.health = health;
		this.level = level;
		this.damage = damage;
		this.essence = essence;
		this.walkspeed = walkspeed;
		node = 0;
	}
	
	/***
	 * Sets a LivingEntity's health and maximum health to the MobData value.
	 * @param le
	 */
	public void initialize(LivingEntity le)
	{
		le.setMaxHealth(health);
		le.setHealth(health);
	}
	
	public MobData clone()
	{
		return new MobData(health, level, damage, essence, walkspeed);
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getDamage()
	{
		return damage;
	}
	
	public int getEssence()
	{
		return essence;
	}
	
	public int getNode()
	{
		return node;
	}
	
	public void incrementNode()
	{
		node++;
	}
	
	public float getWalkspeed()
	{
		return walkspeed;
	}
}