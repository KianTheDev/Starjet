package me.thekian.weapon;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public class WepData 
{
	protected class CWep
	{
		protected String type;
		protected int damage, burn, poison, wither, kb;
		protected String projectileType;
		protected double vMult;
		protected int level;
		
		public CWep(String t, int d, int b, int p, int w, int k, String pr, double v, int l)
		{
			type = t;
			damage = d;
			burn = b;
			poison = p;
			wither = w;
			kb = k;
			projectileType = pr;
			vMult = v;
			level = l;
		}
	}
	protected HashMap<String, CWep> WeaponDataMap = new HashMap<String, CWep>();
	protected void Initialize()
	{
		WeaponDataMap.put(ChatColor.GRAY + ".20 Pistol", new CWep("Gun", 2, 0, 0, 0, 0, "pearl", 1.75, 1));
		WeaponDataMap.put(ChatColor.GRAY + "Laspistol", new CWep("Gun", 4, 1, 0, 0, 0, "snow", 3, 2));
		WeaponDataMap.put(ChatColor.GRAY + "Rifle", new CWep("Gun", 5, 0, 0, 0, 0, "pearl", 2.25, 2));
		WeaponDataMap.put(ChatColor.GRAY + ".50 Rifle", new CWep("Gun", 10, 0, 0, 0, 0, "arrow", 4, 3));
		WeaponDataMap.put(ChatColor.GRAY + "Plasma Pistol", new CWep("Gun", 10, 3, 0, 0, 0, "egg", 3, 5));
		WeaponDataMap.put(ChatColor.GRAY + "Particle Pistol", new CWep("Gun", 8, 0, 0, 0, 2, "egg", 4, 6));
		WeaponDataMap.put(ChatColor.GRAY + "Plasma Rifle", new CWep("Gun", 22, 6, 0, 0, 0, "egg", 4, 8));
		WeaponDataMap.put(ChatColor.GRAY + "Particle Rifle", new CWep("Gun", 18, 3, 0, 0, 3, "egg", 4, 9));
	}
}
