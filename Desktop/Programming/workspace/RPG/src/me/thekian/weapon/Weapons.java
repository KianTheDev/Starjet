package me.thekian.weapon;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.thekian.data.Players.CPlayer;
import me.thekian.items.CItem;
import me.thekian.items.ItemType;
import me.thekian.items.Items;
import me.thekian.weapon.WepData.CWep;
import me.thekian.rpg.RPGCore;

public class Weapons 
{
	public Items itemClass;
	public Plugin plugin;
	public HashMap<Projectile, ProjectileData> ProjDataMap = new HashMap<Projectile, ProjectileData>(), GrenadeDataMap = new HashMap<Projectile, ProjectileData>();
	private WepData wepData = new WepData();
	public class ProjectileData
	{
		private int burn, poison, wither, kb;
		private double damage;
		private Vector direction;
		
		public ProjectileData(double dam, int bur, int poi, int wit, int kno, Vector vec)
		{
			damage = dam;
			burn = bur;
			poison = poi;
			wither = wit;
			kb = kno;
			direction = vec;
		}
		
		public double GetDamage()
		{
			return damage;
		}
		
		public int GetBurn()
		{
			return burn;
		}
		
		public int GetPoison()
		{
			return poison;
		}
		
		public int GetWither()
		{
			return wither;
		}
		
		public int GetKB()
		{
			return kb;
		}
		
		public Vector getDirection()
		{
			return direction;
		}
	}
	
	public void Initialize(Plugin p, Items i)
	{
		itemClass = i;
		plugin = p;
		wepData.Initialize();
	}
	
	public void ItemUse(ItemStack is, Player p, CPlayer cp)
	{
		if(is == null)
			return;
		int i = itemClass.getItemID(is);
		if(i == -1)
			return;
		CItem ci = itemClass.getItems().get(i);
		if(ci.getType().equals(ItemType.WEAPON_RANGED))
		{
			double i2 = itemClass.getItemDamage(is);
			if(i2 == -1)
				return;
			Arrow a = p.launchProjectile(Arrow.class);
			a.setVelocity(p.getLocation().getDirection().multiply(1.25));
			ProjDataMap.put(a, new ProjectileData(i2, 0, 0, 0, 1, p.getLocation().getDirection()));
		}
	}
}
