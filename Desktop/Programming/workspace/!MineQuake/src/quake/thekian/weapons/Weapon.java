package quake.thekian.weapons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import quake.thekian.weapons.WepData.WeaponType;

public class Weapon 
{
	private AttackBase weaponData;
	private double velocity, range;
	private int cooldown, maxCooldown, data;
	private String name, displayName;
	private Material materialType;
	private WeaponType weaponType;
	
	public Weapon(AttackBase weaponData, double velocity, int cooldown, String name, String displayName, Material materialType, int data, WeaponType weaponType, double range)
	{
		this.weaponData = weaponData;
		this.velocity = velocity;
		this.cooldown = cooldown;
		this.maxCooldown = cooldown;
		this.name = name;
		this.displayName = displayName;
		this.materialType = materialType;
		this.weaponType = weaponType;
		this.data = data;
		this.range = range;
	}
	
	public int getCooldown()
	{
		return cooldown;
	}
	
	public String getDisplayName()
	{
		return displayName;
	}
	
	public ItemStack getItem()
	{
		ItemStack is = new ItemStack(materialType);
		System.out.println("Weapon created: " + materialType);
		is.setDurability((short) data);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayName);
		is.setItemMeta(im);
		return is;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getRange()
	{
		return range;
	}
	
	public double getVelocity()
	{
		return velocity;
	}
	
	public AttackBase getWeaponData()
	{
		return weaponData;
	}
	
	public WeaponType getWeaponType()
	{
		return weaponType;
	}
	
	public void tickCooldown()
	{
		if(cooldown > 0)
			cooldown--;
	}
	
	public boolean canFire()
	{
		return cooldown == 0;
	}
	
	public void fireCooldown()
	{
		cooldown = maxCooldown;
	}
}
