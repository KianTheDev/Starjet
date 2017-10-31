package quake.thekian.main;

import quake.thekian.weapons.WepData.WeaponType;

public class WeaponPickup 
{
	private boolean canBeUsed;
	private WeaponType weaponType;
	private int ammoAmount, rechargeTime;
	
	public WeaponPickup(WeaponType weaponType, int ammoAmount, int rechargeTime)
	{
		this.weaponType = weaponType;
		this.ammoAmount = ammoAmount;
		this.rechargeTime = rechargeTime;
		canBeUsed = true;
	}
	
	public WeaponType getWeaponType()
	{
		return weaponType;
	}
	
	public int getAmmoAmount()
	{
		return ammoAmount;
	}
	
	public boolean getCanBeUsed()
	{
		return canBeUsed;
	}
	
	public int getRechargeTime()
	{
		return rechargeTime;
	}
	
	public WeaponPickup createCopy()
	{
		return new WeaponPickup(weaponType, ammoAmount, rechargeTime);
	}
	
	public void setUsed(boolean b)
	{
		canBeUsed = !b;
	}
}
