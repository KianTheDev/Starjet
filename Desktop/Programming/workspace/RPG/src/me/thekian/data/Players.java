package me.thekian.data;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.thekian.items.CItem;
import me.thekian.magic.Ability;

import org.bukkit.Material;

public class Players 
{
	public class CAccount
	{
		private int characterNumber;
		private ArrayList<CCharacter> charList;
		private UUID owner;
		
		public CAccount(int c, ArrayList<CCharacter> list, UUID u)
		{
			characterNumber = c;
			charList = list;
			owner = u;
		}
		
		public int getCharNumber()
		{
			return characterNumber;
		}
		
		public ArrayList<CCharacter> getChars()
		{
			return charList;
		}
		
		public UUID getOwner()
		{
			return owner;
		}
		
		public void setCharNumber(int i)
		{
			characterNumber = i;
		}
	}
	
	public class CCharacter
	{
		private CPlayer playerDat;
		private ArrayList<CItem> items, armor;
		private double[] coords = new double[3];
		private int characterNumber;
		
		public CCharacter(CPlayer pd, ArrayList<CItem> i, ArrayList<CItem> a, int cn)
		{
			playerDat = pd;
			items = i;
			armor = a;
			characterNumber = cn;
		}
		
		public void loadData(Player p, CPlayer cp)
		{
			System.out.println("?-2");
			cp.setCredits(playerDat.getCredits());
			cp.setUUID(playerDat.getUUID());
			cp.setClass(playerDat.getPlayerClass());
			cp.setRace(playerDat.getRace());
			cp.setXPLevel(playerDat.getLevel(), playerDat.getXP());
			cp.setMaxHealth();
			cp.setHealth(playerDat.getHealth());
			p.getInventory().setArmorContents(null);
			p.getInventory().clear();
			new BukkitRunnable(){
				
				public void run()
				{
					if(armor.get(0).getItem(false) != null)
						if(!armor.get(0).getItem(false).getType().equals(Material.AIR))
							p.getInventory().setBoots(armor.get(0).makeCopy().getItem(false));
					if(armor.get(1).getItem(false) != null)
						if(!armor.get(1).getItem(false).getType().equals(Material.AIR))
							p.getInventory().setLeggings(armor.get(1).makeCopy().getItem(false));
					if(armor.get(2).getItem(false) != null)
						if(!armor.get(2).getItem(false).getType().equals(Material.AIR))
							p.getInventory().setChestplate(armor.get(2).makeCopy().getItem(false));
					if(armor.get(3).getItem(false) != null)
						if(!armor.get(3).getItem(false).getType().equals(Material.AIR))
							p.getInventory().setHelmet(armor.get(3).makeCopy().getItem(false));
					for(int i = 0; i < 36; i++)
					{
						if(items.get(i).getItem(false) != null)
								p.getInventory().setItem(i, items.get(i).makeCopy().getItem(false));
					}
				}
			}.run();
			System.out.println("Debug 1");
			new BukkitRunnable(){
				
				public void run()
				{
					System.out.println("Debug 2");
					p.teleport(new Location(p.getWorld(), coords[0], coords[1], coords[2]));
				}
			
			}.run();
		}
		
		public void loadData(Player p, CPlayer cp, Plugin plugin)
		{
			cp.setCredits(playerDat.getCredits());
			cp.setUUID(playerDat.getUUID());
			cp.setClass(playerDat.getPlayerClass());
			cp.setRace(playerDat.getRace());
			cp.setXPLevel(playerDat.getLevel(), playerDat.getXP());
			cp.setMaxHealth();
			cp.setHealth(playerDat.getHealth());
			p.getInventory().setArmorContents(null);
			p.getInventory().clear();
			final Player player = p; 
			new BukkitRunnable(){
				
				public void run()
				{
					if(armor.get(0).getItem(false) != null)
						if(!armor.get(0).getItem(false).getType().equals(Material.AIR))
							player.getInventory().setBoots(armor.get(0).makeCopy().getItem(false));
					if(armor.get(1).getItem(false) != null)
						if(!armor.get(1).getItem(false).getType().equals(Material.AIR))
							player.getInventory().setLeggings(armor.get(1).makeCopy().getItem(false));
					if(armor.get(2).getItem(false) != null)
						if(!armor.get(2).getItem(false).getType().equals(Material.AIR))
							player.getInventory().setChestplate(armor.get(2).makeCopy().getItem(false));
					if(armor.get(3).getItem(false) != null)
						if(!armor.get(3).getItem(false).getType().equals(Material.AIR))
							player.getInventory().setHelmet(armor.get(3).makeCopy().getItem(false));
					for(int i = 0; i < 36; i++)
					{
						if(items.get(i).getItem(false) != null)
								player.getInventory().setItem(i, items.get(i).makeCopy().getItem(false));
					}
					//System.out.println(coords[0] + " " + coords[1] + " " + coords[2]);
					Location loc = new Location(player.getWorld(), coords[0], coords[1], coords[2]);
					player.teleport(loc);
				}
			}.runTaskLater(plugin, 2);
		}
		
		public void setCoords(double x, double y, double z)
		{
			coords[0] = x;
			coords[1] = y;
			coords[2] = z;
		}
		
		public int getCharNum()
		{
			return characterNumber;
		}
		
		public CPlayer getPlayerData()
		{
			return playerDat;
		}
		
		public ArrayList<ArrayList<CItem>> getInventory()
		{
			ArrayList<ArrayList<CItem>> list = new ArrayList<ArrayList<CItem>>();
			list.add(items);
			list.add(armor);
			return list;
		}
		
		public double[] getCoords()
		{
			return coords;
		}
	}
	
	public class CPlayer
	{
		private UUID uuid;
		private PlayerClass playerClass;
		private Race race;
		private int health, maxHealth, xp, level, xproof, credits, statpoints, cooldown;
		//Stats
		private int luck, logic, strength, agility, intelligence, vitality;
		private Ability ability;
		private EnumLang language;
		private double damage;
		
		public CPlayer(UUID uniqueID)
		{
			strength = 0;
			luck = 0;
			logic = 0;
			intelligence = 0;
			agility = 0;
			vitality = 0;
			uuid = uniqueID;
			health = 10;
			maxHealth = 10;
			xp = 0;
			level = 1;
			xproof = 1000;
			playerClass = PlayerClass.FIGHTER;
			credits = 100;
			race = Race.HUMAN;
			cooldown = 0;
			ability = null;
		}
		
		private void LevelUp()
		{
			if(race.equals(Race.PIG))
			{
				statpoints += 3;
			} else
			{
				statpoints += 2;
			}
			level = level + 1;
			xproof = 1000 * level;
			if(xp >= xproof)
			{
				LevelUp();
			}
			if(Bukkit.getPlayer(uuid) != null)
			{
				Bukkit.getPlayer(uuid).sendMessage(ChatColor.BLUE + "RPG> " + ChatColor.GRAY + "You have leveled up!");
			}
			setMaxHealth();
		}
		
		public String AddXP(int exp, int lvl)
		{
			int i2 = exp;
			if(i2 < 0)
			{
				i2 = 0;
			}
			if(level / 2 >= lvl || level - 10 > lvl)
				i2 = 0;
			else if((double) lvl >= (double) level * 1.5D)
				i2 = (int) ((double) i2 * 1.5);
			else
				i2 = (int) (i2 / ((double) level / (double) lvl));
			xp += i2;
			if(xp >= xproof)
			{
				LevelUp();
			}
			if(exp <= 0)
				return ChatColor.BLUE + "RPG> " + ChatColor.GRAY + "You did not get any XP.";
			else
				return ChatColor.BLUE + "RPG> " + ChatColor.GRAY + "You got " + i2 + " XP.";
		}
		
		public void setClass(PlayerClass pc)
		{
			playerClass = pc;
		}
		
		public PlayerClass getPlayerClass()
		{
			return playerClass;
		}
		
		public void setHealth(int i)
		{
			if(i > maxHealth)
				health = maxHealth;
			else
				health = i;
		}
		
		public int getHealth()
		{
			return health;
		}
		
		public void setUUID(UUID u)
		{
			uuid = u;
		}
		
		public UUID getUUID()
		{
			return uuid;
		}
		
		public void setMaxHealth()
		{
			int i = 0;
			if(playerClass.equals(PlayerClass.FIGHTER) || playerClass.equals(PlayerClass.MARINE))
				i = 10;
			else if(playerClass.equals(PlayerClass.TINKERER) || playerClass.equals(PlayerClass.RANGER) || playerClass.equals(PlayerClass.ENGINEER) || playerClass.equals(PlayerClass.SNIPER))
				i = 8;
			else if(playerClass.equals(PlayerClass.MAGICIAN) || playerClass.equals(PlayerClass.TECHNOMANCER))
				i = 6;
			if(race.equals(Race.COW))
				i += 2;
			maxHealth = i * level + vitality * 2;
		}
		
		public int getMaxHealth()
		{
			return maxHealth;
		}
		
		public int getLevel()
		{
			return level;
		}
		
		public int getXP()
		{
			return xp;
		}
		
		public int getXPRoof()
		{
			return xproof;
		}
		
		public void setCredits(int i)
		{
			credits = i;
		}
		
		public int getCredits()
		{
			return credits;
		}
		
		public void setXPLevel(int l, int x)
		{
			xp = x;
			level = l;
			xproof = l * 1000;
		}
		
		public Race getRace()
		{
			return race;
		}
			
		public void setRace(Race r)
		{
			race = r;
		}
		
		public int getStrength()
		{
			return strength;
		}
		
		public int getLuck()
		{
			return luck;
		}
		
		public int getLogic()
		{
			return logic;
		}
		
		public int getAgility()
		{
			return agility;
		}
		
		public int getIntelligence()
		{
			return intelligence;
		}
		
		public int getVitality()
		{
			return vitality;
		}
		
		public int getStatPoints()
		{
			return statpoints;
		}
		
		public void setStrength(int i)
		{
			strength = i;
		}
		
		public void setLuck(int i)
		{
			luck = i;
		}
		
		public void setLogic(int i)
		{
			logic = i;
		}
		
		public void setAgility(int i)
		{
			agility = i;
		}
		
		public void setIntelligence(int i)
		{
			intelligence = i;
		}
		
		public void changeVitality(int i)
		{
			health += i * 2;
			setMaxHealth();
			vitality += i;
		}
		
		public void setStatPoints(int i)
		{
			statpoints = i;
		}
		
		public void setLanguage(EnumLang el)
		{
			language = el;
		}
		
		public EnumLang getLanguage()
		{
			return language;
		}
		
		public void setDamage(double d)
		{
			damage = d;
		}
		
		public double getDamage()
		{
			return damage;
		}
	}
}
