package kian.towers.tower;

import org.bukkit.entity.EntityType;

import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Tower data class. Holds information on the tower's attack. Contains some information superfluous to certain attack types to avoid
 * excessive abstraction. Actual attack implementation is elsewhere.
 * @author TheKian
 *
 */
public class AttackData
{
	private int attacks; //Number of attacks the tower makes
	private int damage; //Damage the tower's attack does or intensity of tower's effect
	private int speed; //Cooldown on the tower's attack or duration of tower's effect
	private int range; //Range from which the tower may affect the enemy
	private int damageLevel; //Degree to which the tower can affect enemies with special resistances; 1 - default, 2 - armored, 3 - flying, 4 - flying or armored, 5 - boss DR
	private AttackType attackType; //Kind of attack the tower uses
	private EntityType projectileType; //For projectile attacks only - Bukkit projectile to fire
	private ParticleTypeEnum particle; //For particle attacks only - NMSLib particle enum entry
	private SplashData splashData; //Defined if there is splash damage - otherwise is null
	private AttackData special; double chance; //Data with a special attack has a likelihood (equal to chance) to activate on a given attack
	
	public AttackData(int attacks, int damage, int speed, int range, int damageLevel, AttackType attackType, EntityType projectileType, ParticleTypeEnum particle)
	{
		this.attacks = attacks;
		this.damage = damage;
		this.speed = speed;
		this.range = range;
		this.damageLevel = damageLevel;
		this.attackType = attackType;
		this.projectileType = projectileType;
		this.particle = particle;
		special = null;
		chance = 0;
	}
	
	/***
	 * Convenience method to change integer values of attack data.
	 * @param type - Which value to change: 0 - attack number, 1 - damage, 2 - attack speed, 3 - attack range, 4 - damage level, 5 - special chance
	 * @param i - Value to pass.
	 */
	public void setData(int type, int i)
	{
		switch(type)
		{
			case 0:
				attacks = i;
				break;
			case 1:
				damage = i;
				break;
			case 2:
				speed = i;
				break;
			case 3:
				range = i;
				break;
			case 4:
				damageLevel = i;
				break;
			case 5:
				chance = i / 10000.0;
				break;
			default:
				return;
		}
	}
	
	/***
	 * Sets the attack's special ability to the AttackData instance passed to it.
	 * @param at - AttackData instance to be passed.
	 */
	public void setSpecial(AttackData at)
	{
		special = at;
	}
	
	//Getter methods
	
	public AttackData getSpecial()
	{
		return special;
	}
	
	public int getAttacks()
	{
		return attacks;
	}
	
	public int getDamage()
	{
		return damage;
	}
	
	public int getSpeed()
	{
		return speed;
	}
	
	public int getRange()
	{
		return range;
	}
	
	public int getDamageLevel()
	{
		return damageLevel;
	}
	
	public SplashData getSplashData()
	{
		return splashData;
	}
	
	public void setSplashData(SplashData splashData)
	{
		this.splashData = splashData;
	}
	
	public AttackType getAttackType()
	{
		return attackType;
	}
	
	public EntityType getProjectileType()
	{
		return projectileType;
	}
	
	public double getSpecialChance()
	{
		return chance;
	}
	
	public ParticleTypeEnum getParticle()
	{
		return particle;
	}
	
}
