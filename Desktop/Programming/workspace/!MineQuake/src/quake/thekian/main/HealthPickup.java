package quake.thekian.main;

public class HealthPickup 
{
	private boolean canBeUsed;
	private int amount, rechargeTime;
	
	public HealthPickup(int amount, int rechargeTime)
	{
		this.amount = amount;
		this.rechargeTime = rechargeTime;
		canBeUsed = true;
	}
	
	public int getHealthAmount()
	{
		return amount;
	}
	
	public boolean getCanBeUsed()
	{
		return canBeUsed;
	}
	
	public int getRechargeTime()
	{
		return rechargeTime;
	}
	
	public HealthPickup createCopy()
	{
		return new HealthPickup(amount, rechargeTime);
	}
	
	public void setUsed(boolean b)
	{
		canBeUsed = !b;
	}
}
