package quake.thekian.weapons;

import quake.thekian.weapons.WepData.ProjectileType;

public class AttackBase 
{
	private double damage;
	private int burn, knockback;
	private ProjectileType projectileType;
	
	private boolean splash;
	private double splash100, splash50, splash25; //Weapon splash radius at 100% damage, 50% damage, and 25% damage
	
	public AttackBase(double damage, int knockback, int burn, //Damage data
			ProjectileType projectileType, boolean splash, //Misc data
			double splash100, double splash50, double splash25) //Splash data
	{
		this.damage = damage;
		this.knockback = knockback;
		this.burn = burn;
		this.projectileType = projectileType;
		this.splash = splash;
		this.splash100 = splash100;
		this.splash50 = splash50;
		this.splash25 = splash25;
	}
	
	public AttackBase baseCopy()
	{
		return new AttackBase(damage, knockback, burn, projectileType, splash, splash100, splash50, splash25);
	}
	
	/***
	 * @param i - Data index to retrieve. 0 - damage; 1 - burn; 2 - knockback.
	 */
	public double getDamageData(int i)
	{
		switch(i)
		{
			case 0: return this.damage;
			case 1: return this.burn;
			case 2: return this.knockback;
			default: return this.damage;
		}
	}
	
	/***
	 * 
	 * @param i - Data index to retrieve. 0 - 100% splash damage radius; 1 - 50% splash damage radius; 2 - 25% splash damage radius.
	 */
	public double getSplashData(int i)
	{
		switch(i)
		{
			case 0: return this.splash100;
			case 1: return this.splash50;
			case 2: return this.splash25;
			default: return this.splash100;
		}
	}
	
	public boolean getDoesSplash()
	{
		return this.splash;
	}
	
	public ProjectileType getProjectileType()
	{
		return projectileType;
	}
}
