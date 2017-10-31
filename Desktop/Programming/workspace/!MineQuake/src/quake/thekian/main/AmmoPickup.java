package quake.thekian.main;

import quake.thekian.weapons.WepData.WeaponType;

public class AmmoPickup 
{
	private boolean canBeUsed;
	private WeaponType ammoType;
	private int ammoAmount, rechargeTime;
	
	public AmmoPickup(WeaponType ammoType, int ammoAmount, int rechargeTime)
	{
		this.ammoType = ammoType;
		this.ammoAmount = ammoAmount;
		this.rechargeTime = rechargeTime;
		canBeUsed = true;
	}
	
	public WeaponType getAmmoType()
	{
		return ammoType;
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
	
	public AmmoPickup createCopy()
	{
		return new AmmoPickup(ammoType, ammoAmount, rechargeTime);
	}
	
	public void setUsed(boolean b)
	{
		canBeUsed = !b;
	}
}
