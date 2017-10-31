package me.thekian.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.thekian.cstmobs.CustomEntityType;
import me.thekian.util.ReflUtil;
import net.minecraft.server.v1_11_R1.EntityCreature;
import net.minecraft.server.v1_11_R1.EntityInsentient;

public class Mobs 
{

	public class MobData
	{

		private String name;
		private CustomEntityType entityType;
		private double maxHealth, damage;
		private int level, xp, nametagType;
		private boolean invulnerable, hostile;
		
		public MobData(String n, CustomEntityType et, double h, boolean i, int lvl, int x, boolean hs, double d, int ntt)
		{
			maxHealth = h;
			name = n;
			entityType = et;
			invulnerable = i;
			level = lvl;
			xp = x;
			hostile = hs;
			damage = d;
			nametagType = ntt;
		}
		
		public CMob createCMob(Location spawn)
		{
			return new CMob(name, entityType, spawn, maxHealth, invulnerable, level, xp, hostile, damage, nametagType);
		}
	}

	public class NPCData
	{

		private String name;
		private CustomEntityType entityType;
		private double maxHealth, damage;
		private int level, xp, nametagType;
		private boolean invulnerable, hostile, shopKeeper;
		private String[] greets;
		private ShopType shop;
		
		public NPCData(String n, CustomEntityType et, double h, boolean i, int lvl, int x, double d, int ntt, boolean hs, boolean sk, String[] gr, ShopType st)
		{
			maxHealth = h;
			name = n;
			entityType = et;
			invulnerable = i;
			level = lvl;
			xp = x;
			damage = d;
			hostile = hs;
			shop = st;
			greets = gr;
			shopKeeper = sk;
			nametagType = ntt;
		}
		
		public CNPC createCNPC(Location spawn)
		{
			return new CNPC(name, entityType, spawn, maxHealth, invulnerable, level, xp, damage, nametagType, greets, shop, shopKeeper);
		}
	}
	
	public class CMob
	{
		private String name;
		private LivingEntity entity;//, nametag;
		private double health, maxHealth, damage, lastDamage;
		private int level, xp;
		//private ArmorStand nametag;
		private Entity nametag;//, nametag2;
		private boolean invulnerable, hostile;
		private Entity lastDamager;
		
		public CMob(String n, CustomEntityType et, Location spawnLoc, double h, boolean i, int lvl, int x, boolean hs, double d, int nametagType)
		{
			lastDamage = 0;
			lastDamager = null;
			health = h;
			maxHealth = h;
			name = n;
			hostile = hs;
			
			try 
			{
				EntityInsentient ei = et.getCustomClass().getConstructor(ReflUtil.getNMSClass("World")).newInstance(((CraftWorld) spawnLoc.getWorld()).getHandle());//.getMethod(et.getCustomClass().getName(), ReflUtil.getNMSClass("World")).invoke(et.getCustomClass(), ((CraftWorld) spawnLoc.getWorld()).getHandle());
				ei.teleportTo(spawnLoc, false);
				entity = (org.bukkit.entity.LivingEntity) ei.getBukkitEntity();
				entity.setRemoveWhenFarAway(false);
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			if(nametagType == 0)
			{
				ItemStack is = new ItemStack(Material.WOOD_BUTTON);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName("NAMETAG_BUTTON");
				is.setItemMeta(im);
				nametag = spawnLoc.getWorld().dropItem(spawnLoc, is);
				((Item) nametag).setItemStack(is);
			} else if(nametagType == 1)
			{
				nametag = spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
				((ArmorStand) nametag).setVisible(false);
				((ArmorStand) nametag).setGravity(false);
				((ArmorStand) nametag).setSmall(true);
			}
			//nametag2 = spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
			//((ArmorStand) nametag2).setVisible(false);
			//((ArmorStand) nametag2).setGravity(false);
			//((ArmorStand) nametag2).setSmall(true);
			ChatColor cc = ChatColor.GREEN;
			if(hostile)
				cc = ChatColor.RED;
			if(invulnerable)
			{
				cc = ChatColor.BLUE;
				nametag.setCustomName(cc + name);
			} else
				nametag.setCustomName(cc + name + ChatColor.GRAY + " - " + ChatColor.RED + (int) (health / maxHealth * 100) + "%");
			nametag.setCustomNameVisible(true);
			entity.setPassenger(nametag);
			//nametag2.setCustomNameVisible(true);
			//nametag2.setCustomName(ChatColor.RED + "||||||||||");
			Location loc = nametag.getLocation();
			//nametag2.teleport(new Location(nametag.getWorld(), loc.getX(), loc.getY() - 0.1, loc.getZ()));

			invulnerable = i;
			level = lvl;
			xp = x;
			damage = d;
		}
		
		public double getDamage()
		{
			return damage;
		}
		
		public LivingEntity getEntity()
		{
			return entity;
		}
		
		public boolean getInvulnerable()
		{
			return invulnerable;
		}
		
		public void setHealth(double d)
		{
			if(!invulnerable)
				health = d;
			else
				health = maxHealth;
			if(health < 0)
				health = 0;
			if(health > maxHealth)
				health = maxHealth;
			ChatColor cc = ChatColor.GREEN;
			if(hostile)
				cc = ChatColor.RED;
			if(invulnerable)
			{
				cc = ChatColor.BLUE;
				nametag.setCustomName(cc + name);
			} else
				nametag.setCustomName(cc + name + ChatColor.GRAY + " - " + ChatColor.RED + (int) (health / maxHealth * 100) + "%");
			entity.setHealth(health / maxHealth * entity.getMaxHealth());
			if(health <= 0)
			{
				((ArmorStand) nametag).setHealth(0);
				//((ArmorStand) nametag2).setHealth(0);
			}
		}
		
		public void setLastDamage(double d)
		{
			lastDamage = d;
		}
		
		public double getLastDamage()
		{
			return lastDamage;
		}
		
		public void setLastDamager(Entity e)
		{
			lastDamager = e;
		}
		
		public Entity getLastDamager()
		{
			return lastDamager;
		}
		
		public void damage(double d)
		{
			if(!invulnerable)
				health -= d;
			else
				health = maxHealth;
			if(health < 0)
				health = 0;
			if(health > maxHealth)
				health = maxHealth;
			ChatColor cc = ChatColor.GREEN;
			if(hostile)
				cc = ChatColor.RED;
			if(invulnerable)
			{
				cc = ChatColor.BLUE;
				nametag.setCustomName(cc + name);
			} else
				nametag.setCustomName(cc + name + ChatColor.GRAY + " - " + ChatColor.RED + (int) (health / maxHealth * 100) + "%");
			entity.setHealth(health / maxHealth * entity.getMaxHealth());
			if(health <= 0)
			{
				((ArmorStand) nametag).setHealth(0);
				//((ArmorStand) nametag2).setHealth(0);
			}
		}
		
		public String getName()
		{
			return name;
		}
		
		public boolean getHostile()
		{
			return hostile;
		}
		
		public double getHealth()
		{
			return health;
		}
		
		public void destroyEnt()
		{
			((Entity) entity).remove();
			nametag.remove();
			//nametag2.remove();
		}
		
		public void updateEnt()
		{
			if((entity.getPassenger() == null || !entity.getPassenger().equals(nametag)) || nametag.getLocation().distance(new Location(entity.getWorld(), entity.getLocation().getX(), entity.getEyeHeight(), entity.getLocation().getZ())) < 0.3)
			{
				nametag.leaveVehicle();
				nametag.teleport(new Location(entity.getWorld(), entity.getLocation().getX(), entity.getEyeHeight(), entity.getLocation().getZ()));
				entity.setPassenger(nametag);
			}
			String s = ChatColor.GREEN + "";
			//nametag2.setCustomNameVisible(true);
			int i = (int) (10.0 * health / maxHealth);
			for(int i2 = 0; i2 < i; i2++)
				s += "|";
			s += ChatColor.GRAY;
			for(int i2 = i; i2 < 10; i2++)
				s += "|";
			//nametag2.setCustomName(s);
			Location loc = nametag.getLocation();
			//nametag2.teleport(new Location(nametag.getWorld(), loc.getX(), loc.getY() - 0.1, loc.getZ()));
		}
	}
	
	public class CNPC extends CMob
	{
		
		private String[] greets;
		private ShopType shop;
		private boolean shopKeeper;
		
		public CNPC(String n, CustomEntityType et, Location spawnLoc, double h, boolean i, int lvl, int x, double d, int nametagType, String[] gr, ShopType st, boolean sk) 
		{
			super(n, et, spawnLoc, h, i, lvl, x, false, d, nametagType);
			greets = gr;
			shop = st;
			shopKeeper = sk;
		}
		
		public void greet(Player p)
		{
			String greetString = greets[(int) (Math.random() * greets.length)];
			p.sendMessage(ChatColor.GREEN + this.getName() + ": " + greetString);
		}
		
		public boolean getShopkeeper()
		{
			return shopKeeper;
		}
		
		public ShopType getShopType()
		{
			return shop;
		}
	}//p.sendMessage(ChatColor.GREEN + "Shopkeeper: " + ( ChatColor.GRAY + "Welcome to my shop! Feel free to look around."));
//String greetString = (ChatColor.GREEN + "Chemist: " + ChatColor.GRAY + "Welcome to my humble bazaar, " + p.getName() + ". You may look around, but touch nothing.");
/*
			int i = 1 + ((int) (Math.random() * 4));
			if(i == 1)
			{
				p.sendMessage(ChatColor.GREEN + "Citizen: " + ChatColor.GRAY + "Hi there!");
			}
			if(i == 2)
			{
				p.sendMessage(ChatColor.GREEN + "Citizen: " + ChatColor.GRAY + "Hello. Who are you?");
			}
			if(i == 3)
			{
				p.sendMessage(ChatColor.GREEN + "Citizen: " + ChatColor.GRAY + "Pardon me.");
			}
			if(i == 4)
			{
				p.sendMessage(ChatColor.GREEN + "Citizen: " + ChatColor.GRAY + "How are you?");
			}
		}
	}*/
}
