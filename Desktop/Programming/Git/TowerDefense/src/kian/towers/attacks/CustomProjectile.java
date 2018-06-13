package kian.towers.attacks;

import org.bukkit.entity.Player;

import kian.towers.tower.AttackData;
import kian.towers.tower.SplashData;

/***
 * Holds information on projectile damage. External to the projectile class, but linked in HashMap.
 */
public class CustomProjectile 
{
	private double damage; //Damage that the projectile does
	private int damageLevel; //Level of enemy that the projectile is able to damage
	private SplashData splash; //Data for AOE effect, if any
	private AttackData special; //Data for special attack, if any
	private double chance; //Chance for special attack to occur
	private Player owner; //Player which owns the projectile
	
	public CustomProjectile(double damage, int damageLevel, SplashData splash, AttackData special, double chance, Player owner)
	{
		this.damage = damage;
		this.damageLevel = damageLevel;
		this.splash = splash;
		this.special = special;
		this.chance = chance;
		this.owner = owner;
	}
	
	//Getter methods
	public double getDamage()
	{
		return damage;
	}
	
	public int getDamageLevel()
	{
		return damageLevel;
	}
	
	public SplashData getSplash()
	{
		return splash;
	}
	
	public AttackData getSpecial()
	{
		return special;
	}
	
	public double getSpecialChance()
	{
		return chance;
	}
	
	public Player getOwner()
	{
		return owner;
	}
}
