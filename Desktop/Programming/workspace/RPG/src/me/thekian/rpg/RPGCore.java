package me.thekian.rpg;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import me.thekian.cstmobs.CustomEntityEvilPig;
import me.thekian.cstmobs.CustomEntityType;
import me.thekian.cstmobs.CustomEntityZombie;
import me.kian.titles.TitleMain;
import me.thekian.cstmobs.CustomEntityEvilCow;
import me.thekian.data.LoadFileData;
import me.thekian.data.MobList;
import me.thekian.data.Mobs;
import me.thekian.data.PlayerClass;
import me.thekian.data.Mobs.CMob;
import me.thekian.data.Mobs.CNPC;
import me.thekian.data.Mobs.MobData;
import me.thekian.data.Players;
import me.thekian.data.Players.CAccount;
import me.thekian.data.Players.CCharacter;
import me.thekian.data.Players.CPlayer;
import me.thekian.data.Race;
import me.thekian.rpg.Shops;
import me.thekian.util.NPCs;
import me.thekian.util.Particles;
import me.thekian.util.ReflUtil;
import me.thekian.weapon.Weapons;
import me.thekian.weapon.Weapons.ProjectileData;
import net.minecraft.server.v1_11_R1.EntityInsentient;
import me.thekian.items.CItem;
import me.thekian.items.ItemType;
import me.thekian.items.Items;

public class RPGCore extends JavaPlugin implements Listener
{
	Plugin plugin = null;
	String regexInt = "\\d+";
	Mobs mobs = new Mobs();
	Players players = new Players();
	Shops shops = new Shops();
	ShopSystem shopSystem = new ShopSystem();
	Weapons guns = new Weapons();
	LoadFileData loadFileData = new LoadFileData();
	List<Entity> entities;
	String pluginPath;
	Items items = new Items();
	Particles particles = new Particles();
	NPCs npcs = new NPCs();
	MobList mobList = new MobList();
	SecondaryEvents secEv = new SecondaryEvents();
	
	@Override
	public void onEnable()
	{
		getConfig().options().copyDefaults(true);
		saveConfig();
		CustomEntityType.registerEntities();
		plugin = this;
		guns.plugin = plugin;
		String s = "";
		for(int i = 0; i < plugin.getDataFolder().getAbsolutePath().indexOf(plugin.getDataFolder().getName()); i++)
		{
			s += plugin.getDataFolder().getAbsolutePath().charAt(i);
		}
		pluginPath = s;
		System.out.println("Server plugin location: " + pluginPath);
		if(!new File(pluginPath + "playerdata").exists())
			new File(pluginPath + "playerdata" + File.separator).mkdirs();
		if(!new File(pluginPath + "inventories").exists())
			new File(pluginPath + "inventories" + File.separator).mkdirs();
		if(!new File(pluginPath + "accounts").exists())
			new File(pluginPath + "accounts" + File.separator).mkdirs();
		items.initialize();
		guns.Initialize(plugin, items);
		loadFileData.init();
		mobList.init();
		shops.init(items);
		shopSystem.init(items);
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(secEv, this);
		secEv.setPlugin(plugin);
		for(World w : Bukkit.getWorlds())
		{
			for(org.bukkit.entity.Entity e : w.getEntities())
				if(e instanceof org.bukkit.entity.LivingEntity && !(e instanceof Player) || e instanceof Item)
				{
					e.remove();
				}
		}
		//refreshEntities();
		/*new BukkitRunnable()
		{
			
			public void run()
			{
				for(World world : Bukkit.getServer().getWorlds())
				{
					world.setTime(13000);
				}
			}
			
		}.runTaskTimer(this, 20, 20);*/
		new BukkitRunnable()
		{
			
			public void run()
			{
				for(CMob cm : MobDataMap.values())
					cm.updateEnt();
				for(CNPC cn : NPCDataMap.values())
					cn.updateEnt();
			}
			
		}.runTaskTimer(plugin, 20, 1);
		new BukkitRunnable()
		{
			
			public void run()
			{
				for(World world : Bukkit.getServer().getWorlds())
				{
					for(Entity e : world.getEntities())
					{
						if(e instanceof Creature)
							if(MobDataMap.containsKey(e))
							{
							
								Creature c = (Creature) e;
								if(!(c.getTarget() instanceof Player))
								{
									for(Entity ent : c.getNearbyEntities(8, 8, 8))
									{
										if(ent instanceof Player)
											c.setTarget((LivingEntity) ent);
									}
								}
							}
						if(e instanceof Player)
						{
							Player p = (Player) e;
							if(PlayerDataMap.containsKey(p.getUniqueId()))
							{
								double health = PlayerDataMap.get(p.getUniqueId()).getPlayerData().getHealth();
								double maxhealth = PlayerDataMap.get(p.getUniqueId()).getPlayerData().getMaxHealth();
								if(health < maxhealth)
								{
									health = health + 1;
									PlayerDataMap.get(p.getUniqueId()).getPlayerData().setHealth((int) health);
								}
								p.setHealth(20 * (health/maxhealth));
							}
						}
					}
				}
			}
			
		}.runTaskTimer(this, 40, 40);
		//Health display
		new BukkitRunnable()
		{
			
			public void run()
			{
				for(Player p : Bukkit.getOnlinePlayers())
				{
					if(!(characterSelection1.contains(p.getUniqueId()) || characterSelection2.contains(p.getUniqueId()) || characterSelection3.contains(p.getUniqueId())))
					{
						if(PlayerDataMap.containsKey(p.getUniqueId()))
						{
							CPlayer cp = PlayerDataMap.get(p.getUniqueId()).getPlayerData();
							TitleMain.getTitles().createHotbar(p, ChatColor.RED + "HP: " + cp.getHealth() + "/" + cp.getMaxHealth());
						}
					}
				}
			}
			
		}.runTaskTimer(this, 40, 5);
	}
	
	@Override
	public void onDisable()
	{
		CustomEntityType.unregisterEntities();
		for(Player p : Bukkit.getOnlinePlayers())
			if(PlayerDataMap.containsKey(p.getUniqueId()))
			{
				PlayerDataMap.get(p.getUniqueId()).setCoords(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
				if(!(characterSelection1.contains(p.getUniqueId()) || characterSelection2.contains(p.getUniqueId()) || characterSelection3.contains(p.getUniqueId())))
				{
					loadFileData.savePlayerData(PlayerDataMap.get(p.getUniqueId()), p.getUniqueId().toString(), pluginPath);
					loadFileData.savePlayerInventory(PlayerDataMap.get(p.getUniqueId()), p.getUniqueId().toString(), pluginPath, p.getInventory());
				}
			}
	}
	
	public void refreshEntities()
	{
		new BukkitRunnable()
		{
		
			public void run()
			{
				for(World world : Bukkit.getWorlds())
				{
					entities = world.getEntities();
					for(Entity e : entities)
					{
						if(e instanceof Villager)
						{ 
							Villager v = (Villager) e;
							Location loc = v.getLocation();
							Profession prof = v.getProfession();
							v.remove();
							Villager villager = (Villager) world.spawnEntity(loc, EntityType.VILLAGER);
							villager.setProfession(prof);
						}
					}
				}
			}
			
		}.runTaskLater(plugin, 2);
	}
	
	public HashMap<UUID, CCharacter> PlayerDataMap = new HashMap<UUID, CCharacter>();
	public HashMap<LivingEntity, CNPC> NPCDataMap = new HashMap<LivingEntity, CNPC>();
	public HashMap<LivingEntity, CMob> MobDataMap = new HashMap<LivingEntity, CMob>();
	public HashMap<UUID, ArrayList<CCharacter>> AccountDataMap = new HashMap<UUID, ArrayList<CCharacter>>();
	public ArrayList<UUID> characterSelection1 = new ArrayList<UUID>(), characterSelection2 = new ArrayList<UUID>(), characterSelection3 = new ArrayList<UUID>();;
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent e)
	{
		e.setJoinMessage(null);
		new BukkitRunnable(){
			@Override
			public void run() {
				refreshEntities();
				Player p = e.getPlayer();
				generateLoginBox(p.getWorld());
				Location loc = new Location(p.getWorld(), 10002, 1, 10002);
				p.teleport(loc);
				loadCharacters(p);
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
		}.runTaskLater(plugin, 2);
	}
	
	public void generateLoginBox(World w)
	{
		for(int i = 0; i < 5; i++)
		{
			for(int i2 = 0; i2 < 5; i2++)
			{
				w.getBlockAt(10000 + i, 0, 10000 + i2).setType(Material.BEDROCK);
			}
		}
		for(int y = 1; y < 4; y++)
		{
			for(int i = 0; i < 5; i++)
			{
				for(int i2 = 0; i2 < 5; i2++)
				{
					if(i2 != 0 && i2 != 4 && i != 0 && i != 4)
						w.getBlockAt(10000 + i, y, 10000 + i2).setType(Material.AIR);
					else
						w.getBlockAt(10000 + i, y, 10000 + i2).setType(Material.BEDROCK);
				}
			}
		}
		for(int i = 0; i < 5; i++)
		{
			for(int i2 = 0; i2 < 5; i2++)
			{
				w.getBlockAt(10000 + i, 4, 10000 + i2).setType(Material.BEDROCK);
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		Player p = e.getPlayer();
		new BukkitRunnable(){
			@Override
			public void run() {
				ItemStack is = new ItemStack(Material.NETHER_STAR);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.BLUE + "Stats");
				is.setItemMeta(im);
				p.getInventory().setItem(8, is);
			}
		}.runTaskLater(plugin, 4);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		e.setQuitMessage(null);
		if(PlayerDataMap.containsKey(e.getPlayer().getUniqueId()))
		{
			PlayerDataMap.get(e.getPlayer().getUniqueId()).setCoords(e.getPlayer().getLocation().getX(), e.getPlayer().getLocation().getY(), e.getPlayer().getLocation().getZ());
			if(!(characterSelection1.contains(e.getPlayer().getUniqueId()) || characterSelection2.contains(e.getPlayer().getUniqueId()) || characterSelection3.contains(e.getPlayer().getUniqueId())))
			{
				loadFileData.savePlayerData(PlayerDataMap.get(e.getPlayer().getUniqueId()), e.getPlayer().getUniqueId().toString(), pluginPath);
				loadFileData.savePlayerInventory(PlayerDataMap.get(e.getPlayer().getUniqueId()), e.getPlayer().getUniqueId().toString(), pluginPath, e.getPlayer().getInventory());
			}
			PlayerDataMap.remove(e.getPlayer().getUniqueId());
		}
		if(AccountDataMap.containsKey(e.getPlayer().getUniqueId()))
		{
			AccountDataMap.remove(e.getPlayer().getUniqueId());
		}
		if(characterSelection1.contains(e.getPlayer().getUniqueId()))
			characterSelection1.remove(e.getPlayer().getUniqueId());
		if(characterSelection2.contains(e.getPlayer().getUniqueId()))
			characterSelection2.remove(e.getPlayer().getUniqueId());
		if(characterSelection3.contains(e.getPlayer().getUniqueId()))
			characterSelection3.remove(e.getPlayer().getUniqueId());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerChat(PlayerChatEvent e)
	{
		String s = e.getMessage();
		e.setCancelled(true);
		Player p = e.getPlayer();
		for(Player p2 : Bukkit.getOnlinePlayers())
		{
			if(p2.getLocation().distance(p.getLocation()) <= 60)
			{
				if(PlayerDataMap.keySet().contains(p.getUniqueId()))
				{
					CPlayer cp = PlayerDataMap.get(p.getUniqueId()).getPlayerData();
					p2.sendMessage(ChatColor.GREEN + "[" + cp.getPlayerClass().toString2().substring(0, 3) + " " + cp.getLevel() + "] " + ChatColor.BLUE + p.getName() + " " + ChatColor.GRAY + s);
				} else
				{
					p2.sendMessage(ChatColor.BLUE + p.getName() + " " + ChatColor.GRAY + s);
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
		if(e.getEntity().getKiller() != null)
		{
			if(!(e.getEntity().getKiller() instanceof Player))
			{
				return;
			} else
			{
				e.setDroppedExp(0);
				e.getDrops().clear();
				Player p = e.getEntity().getKiller();
				CPlayer cp = PlayerDataMap.get(p.getUniqueId()).getPlayerData();
				//CMonster cm = MobDataMap.get(e.getEntity());
				/*if(cm == null)
				{
					//nil
				} else
				{
					if(cm.getHealth() > 0)
					{
						e.getEntity().setHealth(20);
					} else
					{
						cp.AddXP(cm.getXP());
						p.sendMessage(ChatColor.BLUE + "RPG> " + ChatColor.GRAY + "You got " + cm.getXP() + "XP from the " + cm.getName() + "!");
					}
				}
				if(e.getEntityType().equals(EntityType.SPIDER))
				{
					cp.AddXP(100);
					p.sendMessage(ChatColor.BLUE + "RPG> " + ChatColor.GRAY + "You got 100 XP from the spider!");
				}
				if(e.getEntityType().equals(EntityType.CREEPER))
				{
					cp.AddXP(100);
					p.sendMessage(ChatColor.BLUE + "RPG> " + ChatColor.GRAY + "You got 100 XP from the creeper!");
				}
				if(e.getEntityType().equals(EntityType.SKELETON))
				{
					cp.AddXP(100);
					p.sendMessage(ChatColor.BLUE + "RPG> " + ChatColor.GRAY + "You got 100 XP from the skeleton!");
				}*/
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{
		if(e.getDamage() == 0)
			return;
		if(e.getCause().equals(DamageCause.SUFFOCATION) || e.getCause().equals(DamageCause.FALL))
		{
			e.setCancelled(true);
			e.setDamage(0);
		}
		if(e.getEntity() instanceof ArmorStand)
		{
			if(!((ArmorStand) e.getEntity()).isVisible())
				e.setCancelled(true);
		}
		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(!PlayerDataMap.containsKey(p.getUniqueId()))
			{
				e.setCancelled(true);
				return;
			}
			CPlayer cp = PlayerDataMap.get(p.getUniqueId()).getPlayerData();
			double damage = e.getDamage();
			e.setDamage(0);
			cp.setHealth(cp.getHealth() - (int) damage);
			if(cp.getHealth() <= 0)
			{
				cp.setHealth(0);
				p.setHealth(0);
				return;
			}
			double health = cp.getHealth();
		    double maxhealth = cp.getMaxHealth();
			p.setHealth(20 * (health/maxhealth));
		}
		if(e.getEntity() instanceof LivingEntity)
		{
			LivingEntity le = (LivingEntity) e.getEntity();
			if(MobDataMap.containsKey(le) && e.getCause().equals(DamageCause.LAVA))
			{
				CMob cm = MobDataMap.get(le);
				//System.out.println("EntityDamageEvent damage = " + e.getDamage());
				double d = cm.getLastDamage();
				e.setDamage(0);
				if(cm.getInvulnerable())
					e.setCancelled(true);
				else
					cm.damage(d);
			} else if(NPCDataMap.containsKey(le) && e.getCause().equals(DamageCause.LAVA))
			{
				double d = e.getDamage();
				e.setDamage(0);
				CNPC cn = NPCDataMap.get(le);
				if(cn.getInvulnerable())
					e.setCancelled(true);
				else
					cn.damage(d);
			}
			/*LivingEntity le = (LivingEntity) e.getEntity();
			CMob cm = MobDataMap.get(le);
			if(cm != null)
			{
				int damage = (int) e.getDamage();
				e.setDamage(0);
				cm.setHealth(cm.getHealth() - damage);
				le.setHealth(20);
				if(cm.getHealth() <= 0)
				{
					cm.setHealth(0);
					le.setHealth(0);
				}
				return;
			}
			CNPC cNPC = NPCDataMap.get(le);
			if(cNPC != null)
			{
				if(e.getCause().equals(DamageCause.PROJECTILE))
				{
					e.setDamage(0);
					return;
				}
				int damage = (int) e.getDamage();
				e.setDamage(0);
				cNPC.setHealth(cNPC.getHealth() - damage);
				le.setHealth(20);
				if(cNPC.getHealth() <= 0)
				{
					cNPC.setHealth(0);
					le.setHealth(0);
				} 
				return;
			}*/
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Projectile)
		{
			Projectile p = (Projectile) e.getDamager();
			if(guns.ProjDataMap.containsKey(p))
			{
				ProjectileData pd = guns.ProjDataMap.get(p);
				e.setDamage(pd.GetDamage());
				if(pd.GetBurn() > 0)
				{
					e.getEntity().setFireTicks(20 * pd.GetBurn());
				}
				if(e.getEntity() instanceof LivingEntity)
				{
					/*LivingEntity le = (LivingEntity) e.getEntity();
					CNPC cNPC = NPCDataMap.get(le);
					if(cNPC != null)
					{
						int damage = (int) e.getDamage();
						e.setDamage(0);
						cNPC.setHealth(cNPC.getHealth() - damage);
						le.setHealth(20);
						if(cNPC.getHealth() <= 0)
						{
							cNPC.setHealth(0);
							le.setHealth(0);
						} 
						return;
					}
					if(pd.GetPoison() > 0)
					{
						le.addPotionEffect(PotionEffectType.POISON.createEffect(20 * pd.GetPoison(), 1));
					}
					if(pd.GetWither() > 0)
					{
						le.addPotionEffect(PotionEffectType.WITHER.createEffect(20 * pd.GetPoison(), 1));
					}
					if(pd.GetKB() > 0)
					{
						le.setVelocity(pd.getDirection().multiply(pd.GetKB()));
					}
				}*/
				}
			}
		} else if(e.getDamager() instanceof Player)
		{
			Player p = (Player) e.getDamager();
			LivingEntity le = (LivingEntity) e.getEntity();
			if(MobDataMap.containsKey(le))
			{
				e.setDamage(0);
				MobDataMap.get(le).setLastDamage(PlayerDataMap.get(p.getUniqueId()).getPlayerData().getDamage());
				MobDataMap.get(le).setLastDamager(p);
				MobDataMap.get(le).damage(MobDataMap.get(le).getLastDamage());
				ArmorStand as = (ArmorStand) le.getWorld().spawnEntity(new Location(le.getWorld(), le.getLocation().getX() + ((Math.random() * 2) - 1), le.getLocation().getY() + ((Math.random() * 0.4) - 1), le.getLocation().getZ() + ((Math.random() * 2) - 1)), EntityType.ARMOR_STAND);
				as.setVisible(false);
				as.setGravity(false);
				as.setCustomNameVisible(true);
				ChatColor cc = ChatColor.RED;
				double d = MobDataMap.get(le).getLastDamage();
				if(d == 0)
					cc = ChatColor.GRAY;
				else if(d < 0)
					cc = ChatColor.GREEN;
				as.setCustomName(cc + "" + (int) d * -1);
				new BukkitRunnable(){
				
					public void run()
					{
						as.setHealth(0);
					}
				
				}.runTaskLater(plugin, 30);
				//System.out.println("Damage = " + PlayerDataMap.get(p.getUniqueId()).getPlayerData().getDamage());
				//System.out.println("Mob health = " + MobDataMap.get(le).getHealth());
			}
		} else if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(!PlayerDataMap.containsKey(p.getUniqueId()))
			{
				e.setCancelled(true);
				return;
			}
			CPlayer cp = PlayerDataMap.get(p.getUniqueId()).getPlayerData();
			double damage = e.getDamage();
			e.setDamage(0);
			cp.setHealth(cp.getHealth() - (int) damage);
			if(cp.getHealth() <= 0)
			{
				cp.setHealth(0);
				p.setHealth(0);
				return;
			}
			double health = cp.getHealth();
		    double maxhealth = cp.getMaxHealth();
			p.setHealth(20 * (health/maxhealth));
		} else if(e.getDamager() instanceof LivingEntity && e.getEntity() instanceof Player)
		{
			LivingEntity le = (LivingEntity) e.getDamager();
			Player p = (Player) e.getEntity();
			if(MobDataMap.containsKey(le))
			{
				e.setDamage(MobDataMap.get(le).getDamage());
			}
			/*LivingEntity le = (LivingEntity) e.getDamager();
			CMonster cm = MobDataMap.get(le);
			if(cm == null)
			{
				return;
			} else
			{
				e.setDamage(cm.getDamage());
			}
			CNPC cNPC = NPCDataMap.get(le);
			if(cNPC == null)
			{
				return;
			} else
			{
				return;
			}*/
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e)
	{
		if(e.getCause().equals(TeleportCause.ENDER_PEARL))
		{
			e.setCancelled(true);
		}
	}
	
	/*@EventHandler
	public void onEntityTargetChange(EntityTargetLivingEntityEvent e)
	{
		if(e.getEntity() instanceof Zombie)
		{
			Zombie z = (Zombie) e.getEntity();
			if(!(e.getTarget() instanceof Player))
			{
				new BukkitRunnable()
				{
				
					public void run()
					{
						z.setTarget(null);
					}
				}.runTaskLater(plugin, 1);
			} 
		}
	}*/
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		e.setDeathMessage(null);
		e.setKeepInventory(true);
		new BukkitRunnable()
		{

			@Override
			public void run()
			{
				CPlayer cp = PlayerDataMap.get(e.getEntity().getUniqueId()).getPlayerData();
				cp.setHealth(cp.getHealth());
				e.getEntity().spigot().respawn();
			}
		}.runTaskLater(plugin, 1);
	}
	
	public boolean spawnCMob(int id, Location loc)
	{
		if(id >= mobList.getMobList().size())
			return false;
		CMob cm = mobList.getMobList().get(id).createCMob(loc);
		MobDataMap.put(cm.getEntity(), cm);
		return true;
	}
	
	public boolean spawnCNPC(int id, Location loc)
	{
		if(id >= mobList.getNPCList().size())
			return false;
		CNPC cn = mobList.getNPCList().get(id).createCNPC(loc);
		NPCDataMap.put(cn.getEntity(), cn);
		return true;
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
	{
		if(e.getRightClicked() instanceof Villager)
			e.setCancelled(true);
		if(NPCDataMap.containsKey(e.getRightClicked()))
		{
			CNPC cn = NPCDataMap.get(e.getRightClicked());
			cn.greet(e.getPlayer());
			if(cn.getShopkeeper())
				shops.createShop(cn.getShopType(), e.getPlayer());
			/*Villager v = (Villager) e.getRightClicked();
			e.setCancelled(true);
			CNPC cNPC = NPCDataMap.get(v);
			if(cNPC == null)
			{
				return;
			} else
			{
				cNPC.Greet(e.getPlayer());
			}
			if(cNPC instanceof CBattleShop)
			{
				CBattleShop cbs = (CBattleShop) NPCDataMap.get(v);
				shops.createShopOne(Bukkit.getServer().createInventory(null, cbs.getInvSize(), "Shop"), e.getPlayer());
			}
			if(cNPC instanceof CChemShop)
			{
				CChemShop ccs = (CChemShop) NPCDataMap.get(v);
				shops.createShopTwo(Bukkit.getServer().createInventory(null, ccs.getInvSize(), "Shop"), e.getPlayer());
			}*/
		}
	}
	
	@EventHandler
	public void onPlayerSelectItem(PlayerItemHeldEvent e)
	{
		new BukkitRunnable(){

			public void run()
			{
				Player p = e.getPlayer();
				if(!PlayerDataMap.containsKey(p.getUniqueId()))
					return;
				if(p.getItemInHand() == null)
					return;
				if(items.getItemID(p.getItemInHand()) == -1)
					return;
				if(items.getItemDamage(p.getItemInHand()) == -1)
					PlayerDataMap.get(p.getUniqueId()).getPlayerData().setDamage(1);
				else
					if(items.getItems().get(items.getItemID(p.getItemInHand())).getType().equals(ItemType.WEAPON_MELEE))
						PlayerDataMap.get(p.getUniqueId()).getPlayerData().setDamage((items.getItemDamage(p.getItemInHand())));
			}

		}.runTaskLater(plugin, 2);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		if(p.getItemInHand().getType().equals(Material.EYE_OF_ENDER))
			e.setCancelled(true);
		if(e.getClickedBlock() != null)
		{
			org.bukkit.block.Block cb = e.getClickedBlock();
			if(!p.isOp() && (cb.getType().equals(Material.TRAP_DOOR) || cb.getType().equals(Material.STONE_BUTTON) || cb.getType().equals(Material.ITEM_FRAME)))
				e.setCancelled(true);
		}
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			ItemStack i = e.getPlayer().getItemInHand();
			if(PlayerDataMap.containsKey(p.getUniqueId()))
			{
				guns.ItemUse(i, p, PlayerDataMap.get(p.getUniqueId()).getPlayerData());
			}
			if((i.getType().equals(Material.NETHER_STAR)) && (i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Stats")))
			{
				makeInventory(p, "stats");
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e)
	{
		ItemStack is = e.getItemDrop().getItemStack();
		if(is.getType().equals(Material.NETHER_STAR))
		{
			e.setCancelled(true);
		}
		int i = items.getItemID(is);
		if(i != -1)
		{
			if(items.getItems().get(i).getUndroppable())
			{
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.DARK_RED + "You may not drop this item!");
			}
		}
	}
	
	private void initPlayerInventory(Player p)
	{
		final Player player = p;
		characterSelection3.remove(p.getUniqueId());
		PlayerDataMap.get(p.getUniqueId()).setCoords(p.getWorld().getSpawnLocation().getX(), p.getWorld().getSpawnLocation().getY(), p.getWorld().getSpawnLocation().getZ());
		PlayerDataMap.get(p.getUniqueId()).loadData(p, PlayerDataMap.get(p.getUniqueId()).getPlayerData(), plugin);
		CPlayer cp = PlayerDataMap.get(p.getUniqueId()).getPlayerData();
		ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
		ItemMeta im = itemStack.getItemMeta();
		im.setDisplayName(ChatColor.BLUE + "Stats");
		itemStack.setItemMeta(im);
		p.getInventory().setItem(8, itemStack);
		if(cp.getPlayerClass().equals(PlayerClass.ENGINEER))
			p.getInventory().addItem(items.getItems().get(6).getItem(false));
		else if(cp.getPlayerClass().equals(PlayerClass.FIGHTER))
			p.getInventory().addItem(items.getItems().get(3).getItem(false));
		else if(cp.getPlayerClass().equals(PlayerClass.MAGICIAN))
			p.getInventory().addItem(items.getItems().get(5).getItem(false));
		else if(cp.getPlayerClass().equals(PlayerClass.RANGER))
			p.getInventory().addItem(items.getItems().get(4).getItem(false));
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR))
		{
			return;
		}
		Player p = null;
		if(e.getWhoClicked() instanceof Player)
		{
			p = (Player) e.getWhoClicked();
		} else
		{
			return;
		}
		CPlayer cp = null;
		if(PlayerDataMap.containsKey(p.getUniqueId()))
			cp = PlayerDataMap.get(p.getUniqueId()).getPlayerData();
		//RPG store system
		if(e.getInventory().getName().equals("Shop"))
		{
			shopSystem.ShopClick(p, e.getCurrentItem(), e.getInventory(), cp);
			e.setCancelled(true);
		}
		//~~~
		ItemStack is = e.getCurrentItem();
		if((is.getType().equals(Material.NETHER_STAR)) && (is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Stats")))
		{
			p.closeInventory();
			return;
		}
		if(e.getInventory().getName().equals("Stats"))
		{
			e.setCancelled(true);  
			if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Ability Scores"))
			{
				p.closeInventory();
				makeInventory(p, "statpoints");
			}
		} else if(e.getInventory().getName().equals("Pick your Class"))
		{
			e.setCancelled(true); 
			if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Fighter"))
			{
				PlayerDataMap.get(p.getUniqueId()).getPlayerData().setClass(PlayerClass.FIGHTER);
				if(characterSelection2.contains(p.getUniqueId()))
				{
					characterSelection2.remove(p.getUniqueId());
					characterSelection3.add(p.getUniqueId());
				}
				p.closeInventory();
				makeInventory(p, "chooserace");
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Ranger"))
			{
				PlayerDataMap.get(p.getUniqueId()).getPlayerData().setClass(PlayerClass.RANGER);
				if(characterSelection2.contains(p.getUniqueId()))
				{
					characterSelection2.remove(p.getUniqueId());
					characterSelection3.add(p.getUniqueId());
				}
				p.closeInventory();
				makeInventory(p, "chooserace");
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Magician"))
			{
				PlayerDataMap.get(p.getUniqueId()).getPlayerData().setClass(PlayerClass.MAGICIAN);
				if(characterSelection2.contains(p.getUniqueId()))
				{
					characterSelection2.remove(p.getUniqueId());
					characterSelection3.add(p.getUniqueId());
				}
				p.closeInventory();
				makeInventory(p, "chooserace");
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Tinkerer"))
			{
				PlayerDataMap.get(p.getUniqueId()).getPlayerData().setClass(PlayerClass.TINKERER);
				if(characterSelection2.contains(p.getUniqueId()))
				{
					characterSelection2.remove(p.getUniqueId());
					characterSelection3.add(p.getUniqueId());
				}
				p.closeInventory();
				makeInventory(p, "chooserace");
			}
		} else if(e.getInventory().getName().equals("Pick your Race"))
		{
			e.setCancelled(true); 
			if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Human"))
			{
				PlayerDataMap.get(p.getUniqueId()).getPlayerData().setRace(Race.HUMAN);
				if(characterSelection3.contains(p.getUniqueId()))
				{
					initPlayerInventory(p);
				}
				p.closeInventory();
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Cow"))
			{
				PlayerDataMap.get(p.getUniqueId()).getPlayerData().setRace(Race.COW);
				final Player player = p;
				new BukkitRunnable(){
				
					public void run()
					{
						player.getInventory().addItem(items.getItems().get(4).getItem(false));
						player.getInventory().addItem(items.getItems().get(6).getItem(false));
					}
					
				}.runTaskLater(plugin, 1);
				if(characterSelection3.contains(p.getUniqueId()))
				{
					initPlayerInventory(p);
				}
				p.closeInventory();
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Pig"))
			{
				PlayerDataMap.get(p.getUniqueId()).getPlayerData().setRace(Race.PIG);
				PlayerDataMap.get(p.getUniqueId()).getPlayerData().setIntelligence(2);
				final Player player = p;
				new BukkitRunnable(){
				
					public void run()
					{
						player.getInventory().addItem(items.getItems().get(4).getItem(false));
						player.getInventory().addItem(items.getItems().get(6).getItem(false));
					}
					
				}.runTaskLater(plugin, 1);
				if(characterSelection3.contains(p.getUniqueId()))
				{
					initPlayerInventory(p);
				}
				p.closeInventory();
			}
		} else if(e.getInventory().getName().equals("Ability Scores"))
		{
			e.setCancelled(true); 
			if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Strength: " + ChatColor.YELLOW + cp.getStrength()))
			{
				if(cp.getStatPoints() > 0)
				{
					p.closeInventory();
					makeInventory(p, "abilityscore-Strength");
				} else
				{
					p.closeInventory();
					p.sendMessage(ChatColor.DARK_RED + "You do not have enough stat points available.");
				}
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Luck: " + ChatColor.YELLOW + cp.getLuck()))
			{
				if(cp.getStatPoints() > 0)
				{
					p.closeInventory();
					makeInventory(p, "abilityscore-Luck");
				} else
				{
					p.closeInventory();
					p.sendMessage(ChatColor.DARK_RED + "You do not have enough stat points available.");
				}
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Logic: " + ChatColor.YELLOW + cp.getLogic()))
			{
				if(cp.getStatPoints() > 0)
				{
					p.closeInventory();
					makeInventory(p, "abilityscore-Logic");
				} else
				{
					p.closeInventory();
					p.sendMessage(ChatColor.DARK_RED + "You do not have enough stat points available.");
				}
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Agility: " + ChatColor.YELLOW + cp.getAgility()))
			{
				if(cp.getStatPoints() > 0)
				{
					p.closeInventory();
					makeInventory(p, "abilityscore-Agility");
				} else
				{
					p.closeInventory();
					p.sendMessage(ChatColor.DARK_RED + "You do not have enough stat points available.");
				}
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Intelligence: " + ChatColor.YELLOW + cp.getIntelligence()))
			{
				if(cp.getStatPoints() > 0)
				{
					p.closeInventory();
					makeInventory(p, "abilityscore-Intelligence");
				} else
				{
					p.closeInventory();
					p.sendMessage(ChatColor.DARK_RED + "You do not have enough stat points available.");
				}
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Vitality: " + ChatColor.YELLOW + cp.getVitality()))
			{
				if(cp.getStatPoints() > 0)
				{
					p.closeInventory();
					makeInventory(p, "abilityscore-Vitality");
				} else
				{
					p.closeInventory();
					p.sendMessage(ChatColor.DARK_RED + "You do not have enough stat points available.");
				}
			}
		} else if(e.getInventory().getName().equalsIgnoreCase("Confirm Selection"))
		{
			e.setCancelled(true);
			if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Increase Strength"))
			{
				cp.setStrength(cp.getStrength() + 1);
				cp.setStatPoints(cp.getStatPoints() - 1);
				p.closeInventory();
				makeInventory(p, "statpoints");
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Increase Luck"))
			{
				cp.setLuck(cp.getLuck() + 1);
				cp.setStatPoints(cp.getStatPoints() - 1);
				p.closeInventory();
				makeInventory(p, "statpoints");
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Increase Logic"))
			{
				cp.setLogic(cp.getLogic() + 1);
				cp.setStatPoints(cp.getStatPoints() - 1);
				p.closeInventory();
				makeInventory(p, "statpoints");
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Increase Agility"))
			{
				cp.setAgility(cp.getAgility() + 1);
				cp.setStatPoints(cp.getStatPoints() - 1);
				p.closeInventory();
				makeInventory(p, "statpoints");
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Increase Intelligence"))
			{
				cp.setIntelligence(cp.getIntelligence() + 1);
				cp.setStatPoints(cp.getStatPoints() - 1);
				p.closeInventory();
				makeInventory(p, "statpoints");
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Increase Vitality"))
			{
				cp.changeVitality(1);
				cp.setStatPoints(cp.getStatPoints() - 1);
				p.closeInventory();
				makeInventory(p, "statpoints");
			} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Cancel"))
			{
				p.closeInventory();
				makeInventory(p, "stats");
			}
		} else if(e.getInventory().getName().equalsIgnoreCase("Select your Character"))
		{
			e.setCancelled(true);
			if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Use character"))
			{
				if(e.getInventory().getItem(10).equals(e.getCurrentItem()))
				{
					characterSelection1.remove(p.getUniqueId());
					p.closeInventory();
					final Player player = p;
					new BukkitRunnable(){
						
						public void run()
						{
							CCharacter cc = AccountDataMap.get(player.getUniqueId()).get(0);
							PlayerDataMap.put(player.getUniqueId(), cc);
							cc.loadData(player, cc.getPlayerData(), plugin);
							ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
							ItemMeta im = itemStack.getItemMeta();
							im.setDisplayName(ChatColor.BLUE + "Stats");
							itemStack.setItemMeta(im);
							player.getInventory().setItem(8, itemStack);
						}
					}.runTaskLater(plugin, 4);
				} else if(e.getInventory().getItem(12).equals(e.getCurrentItem()))
				{
					characterSelection1.remove(p.getUniqueId());
					p.closeInventory();
					final Player player = p;
					new BukkitRunnable(){
						
						public void run()
						{
							CCharacter cc = AccountDataMap.get(player.getUniqueId()).get(1);
							PlayerDataMap.put(player.getUniqueId(), cc);
							cc.loadData(player, cc.getPlayerData(), plugin);
							ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
							ItemMeta im = itemStack.getItemMeta();
							im.setDisplayName(ChatColor.BLUE + "Stats");
							itemStack.setItemMeta(im);
							player.getInventory().setItem(8, itemStack);
						}
					}.runTaskLater(plugin, 4);
				} else if(e.getInventory().getItem(14).equals(e.getCurrentItem()))
				{
					characterSelection1.remove(p.getUniqueId());
					p.closeInventory();
					final Player player = p;
					new BukkitRunnable(){
						
						public void run()
						{
							CCharacter cc = AccountDataMap.get(player.getUniqueId()).get(2);
							PlayerDataMap.put(player.getUniqueId(), cc);
							cc.loadData(player, cc.getPlayerData(), plugin);
							ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
							ItemMeta im = itemStack.getItemMeta();
							im.setDisplayName(ChatColor.BLUE + "Stats");
							itemStack.setItemMeta(im);
							player.getInventory().setItem(8, itemStack);
						}
					}.runTaskLater(plugin, 4);
				} else if(e.getInventory().getItem(16).equals(e.getCurrentItem()))
				{
					characterSelection1.remove(p.getUniqueId());
					p.closeInventory();
					final Player player = p;
					new BukkitRunnable(){
						
						public void run()
						{
							CCharacter cc = AccountDataMap.get(player.getUniqueId()).get(0);
							PlayerDataMap.put(player.getUniqueId(), cc);
							cc.loadData(player, cc.getPlayerData(), plugin);
							ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
							ItemMeta im = itemStack.getItemMeta();
							im.setDisplayName(ChatColor.BLUE + "Stats");
							itemStack.setItemMeta(im);
							player.getInventory().setItem(8, itemStack);
						}
					}.runTaskLater(plugin, 4);
				} else
				{
					characterSelection1.remove(p.getUniqueId());
					p.kickPlayer(ChatColor.RED + "Significant inventory GUI error. Our apologies for the inconvenience.");
				}
			} if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Create character"))
			{
				ArrayList<CItem> list1 = new ArrayList<CItem>();
				for(int i = 0; i < 4; i++)
				{
					list1.add(items.getItems().get(0).makeCopy());
				}
				ArrayList<CItem> list2 = new ArrayList<CItem>();
				for(int i = 0; i < 36; i++)
				{
					list2.add(items.getItems().get(0).makeCopy());
				}
				if(AccountDataMap.get(p.getUniqueId()) == null)
					AccountDataMap.put(p.getUniqueId(), new ArrayList<CCharacter>());
				CCharacter cc = players.new CCharacter(players.new CPlayer(p.getUniqueId()), list2, list1, AccountDataMap.get(p.getUniqueId()).size() + 1);
				AccountDataMap.get(p.getUniqueId()).add(cc);
				PlayerDataMap.put(p.getUniqueId(), cc);
				characterSelection1.remove(p.getUniqueId());
				characterSelection2.add(p.getUniqueId());
				makeInventory(p, "chooseclass");
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		Player p = (Player) e.getPlayer();
		if(e.getInventory().getName().equalsIgnoreCase("Select your Character") && characterSelection1.contains(p.getUniqueId()))
		{
			new BukkitRunnable(){
			
				public void run()
				{
					if(characterSelection1.contains(p.getUniqueId()))
						loadCharacters(p);
				}
			}.runTaskLater(plugin, 2);
		} else if(e.getInventory().getName().equalsIgnoreCase("Choose your Class") && characterSelection2.contains(p.getUniqueId()))
		{
			new BukkitRunnable(){

				public void run()
				{
					if(characterSelection2.contains(p.getUniqueId()))
						makeInventory(p, "chooseclass");
				}
			}.runTaskLater(plugin, 2);
		} else if(e.getInventory().getName().equalsIgnoreCase("Choose your Race") && characterSelection3.contains(p.getUniqueId()))
		{
			new BukkitRunnable(){
			
				public void run()
				{
					if(characterSelection3.contains(p.getUniqueId()))
						makeInventory(p, "chooserace");
				}
			}.runTaskLater(plugin, 2);
		}
	}
	
	public static boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?");
	}
	
	public void makeInventory(Player p, String s)
	{
		if(s.equalsIgnoreCase("chooseclass"))
		{
			Inventory inv = Bukkit.getServer().createInventory(null, 27, "Pick your Class");
			ItemStack is = new ItemStack(Material.IRON_SWORD);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.BLUE + "Fighter");
			is.setItemMeta(im);
			inv.setItem(10, is);
			is.setType(Material.BOW);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.BLUE + "Ranger");
			is.setItemMeta(im);
			inv.setItem(12, is);
			is.setType(Material.BOOK);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.BLUE + "Magician");
			is.setItemMeta(im);
			inv.setItem(14, is);
			is.setType(Material.FURNACE);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.BLUE + "Tinkerer");
			is.setItemMeta(im);
			inv.setItem(16, is);
			p.openInventory(inv);
		} else if(s.equalsIgnoreCase("chooserace"))
		{
			Inventory inv = Bukkit.getServer().createInventory(null, 27, "Pick your Race");
			ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta im = (SkullMeta) is.getItemMeta();
			im.setDisplayName(ChatColor.BLUE + "Human");
			im.setLore(Arrays.asList(ChatColor.GRAY + "+2% XP"));
			is.setItemMeta(im);
			inv.setItem(11, is);
			im = (SkullMeta) is.getItemMeta();
			im.setOwner("MHF_Cow");
			im.setDisplayName(ChatColor.BLUE + "Cow");
			im.setLore(Arrays.asList(ChatColor.GRAY + "+2 HP per level", ChatColor.GRAY + "+3 bonus defense per level"));
			is.setItemMeta(im);
			inv.setItem(13, is);
			im = (SkullMeta) is.getItemMeta();
			im.setOwner("MHF_Pig");
			im.setDisplayName(ChatColor.BLUE + "Pig");
			im.setLore(Arrays.asList(ChatColor.GRAY + "+2 starting intelligence", ChatColor.GRAY + "+1 stat point per level"));
			is.setItemMeta(im);
			inv.setItem(15, is);
			im.setOwner("Steve");
			im.setDisplayName(ChatColor.BLUE + "Elf");
			im.setLore(Arrays.asList("Double agility"));
			p.openInventory(inv);
		} else if(s.equalsIgnoreCase("stats"))
		{
			CPlayer cp = PlayerDataMap.get(p.getUniqueId()).getPlayerData();
			Inventory inv = Bukkit.getServer().createInventory(null, 27, "Stats");
			ItemStack sk = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta sm = (SkullMeta) sk.getItemMeta();
			sm.setDisplayName(ChatColor.DARK_PURPLE + p.getName());
			sm.setOwner(p.getName());
			sm.setLore(Arrays.asList(ChatColor.RED + "Level: " + cp.getLevel(), ChatColor.RED + "Race: " + cp.getRace().toString2(), ChatColor.RED + "Available stat points: " + cp.getStatPoints()));
			sk.setItemMeta(sm);
			inv.setItem(4, sk);
			ItemStack is = new ItemStack(Material.EXP_BOTTLE);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "XP: " + ChatColor.YELLOW + cp.getXP() + "/" + cp.getXPRoof() );
			is.setItemMeta(im);
			inv.setItem(10, is);
			is.setType(Material.RAW_BEEF);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "HP: " + ChatColor.YELLOW + cp.getHealth() + "/" + cp.getMaxHealth() );
			is.setItemMeta(im);
			inv.setItem(16, is);
			is.setType(Material.BOW);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GRAY + "Damage: " + cp.getDamage());
			is.setItemMeta(im);
			inv.setItem(13, is);
			is.setType(Material.IRON_SWORD);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Class: " + ChatColor.YELLOW + cp.getPlayerClass().toString2());
			Set<ItemFlag> set = im.getItemFlags();
			for(ItemFlag itemFlag : set)
			{
				im.removeItemFlags(itemFlag);
			}
			is.setItemMeta(im);
			inv.setItem(12, is);
			is.setType(Material.GOLD_NUGGET);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Credits: " + ChatColor.YELLOW + cp.getCredits() );
			is.setItemMeta(im);
			inv.setItem(14, is);
			//Stats
			is.setType(Material.BOOK);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Enhancements" );
			is.setItemMeta(im);
			inv.setItem(21, is);
			p.openInventory(inv);  
			is.setType(Material.EMERALD);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Ability Scores" );
			is.setItemMeta(im);
			inv.setItem(23, is);
			p.openInventory(inv); 
		} else if(s.equalsIgnoreCase("statpoints"))
		{
			CPlayer cp = PlayerDataMap.get(p.getUniqueId()).getPlayerData();
			Inventory inv = Bukkit.getServer().createInventory(null, 27, "Ability Scores");
			ItemStack is = new ItemStack(Material.EMERALD);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Strength: " + ChatColor.YELLOW + cp.getStrength());
			im.setLore(Arrays.asList("Stat points available: " + cp.getStatPoints()));
			is.setItemMeta(im);
			inv.setItem(1, is);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Luck: " + ChatColor.YELLOW + cp.getLuck());
			im.setLore(Arrays.asList("Stat points available: " + cp.getStatPoints()));
			is.setItemMeta(im);
			inv.setItem(4, is);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Logic: " + ChatColor.YELLOW + cp.getLogic());
			im.setLore(Arrays.asList("Stat points available: " + cp.getStatPoints()));
			is.setItemMeta(im);
			inv.setItem(7, is);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Agility: " + ChatColor.YELLOW + cp.getAgility());
			im.setLore(Arrays.asList("Stat points available: " + cp.getStatPoints()));
			is.setItemMeta(im);
			inv.setItem(19, is);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Intelligence: " + ChatColor.YELLOW + cp.getIntelligence());
			im.setLore(Arrays.asList("Stat points available: " + cp.getStatPoints()));
			is.setItemMeta(im);
			inv.setItem(22, is);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Vitality: " + ChatColor.YELLOW + cp.getVitality());
			im.setLore(Arrays.asList("Stat points available: " + cp.getStatPoints()));
			is.setItemMeta(im);
			inv.setItem(25, is);
			p.openInventory(inv); 
		} else if(s.startsWith("abilityscore-"))
		{
			String s2 = s.substring(13);
			Inventory inv = Bukkit.getServer().createInventory(null, 9, "Confirm Selection");
			ItemStack is = new ItemStack(Material.EMERALD_BLOCK);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Increase " + s2);
			is.setItemMeta(im);
			inv.setItem(1, is);
			is.setType(Material.REDSTONE_BLOCK);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Cancel");
			is.setItemMeta(im);
			inv.setItem(7, is);
			p.openInventory(inv);
		}
	}
	
	public ItemStack getHead(String s, String s2)
	{
		ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta im = (SkullMeta) itemStack.getItemMeta();
		im.setOwner(s);
		im.setDisplayName(ChatColor.BLUE + s2);
		itemStack.setItemMeta(im);
		return itemStack;
	}
	
	public void loadCharacters(Player p)
	{
		Inventory inv = Bukkit.createInventory(null, 36, "Select your Character");
		ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE);
		is.setDurability((short) 15);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(" ");
		is.setItemMeta(im);
		for(int i = 0; i < 9; i++)
		{
			inv.setItem(i, is);
			inv.setItem(i + 27, is);
		}
		inv.setItem(9, is);
		inv.setItem(18, is);
		inv.setItem(17, is);
		inv.setItem(26, is);
		if(!characterSelection1.contains(p.getUniqueId()))
			characterSelection1.add(p.getUniqueId());
		ArrayList<CCharacter> charList = loadFileData.loadAccountData(p.getUniqueId(), pluginPath);
		if(!AccountDataMap.containsKey(p.getUniqueId()))
		{
			if(charList != null)
				AccountDataMap.put(p.getUniqueId(), charList);
		} else
		{
			charList = AccountDataMap.get(p.getUniqueId());
		}
		if(charList == null)
		{
			charList = new ArrayList<CCharacter>();
			AccountDataMap.put(p.getUniqueId(), charList);
			is.setType(Material.WOOL);
			is.setDurability((short) 15);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Create character");
			is.setItemMeta(im);
			for(int i = 10; i < 17; i += 2)
			{
				inv.setItem(i, is);
			}
		} else if(charList.size() == 0)
		{
			is.setType(Material.WOOL);
			is.setDurability((short) 15);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.RED + "Create character");
			is.setItemMeta(im);
			for(int i = 10; i < 17; i += 2)
			{
				inv.setItem(i, is);
			}
		} else
		{
			for(int i = 0; i < charList.size(); i++)
			{
				CCharacter cc = charList.get(i);
				is.setType(Material.STAINED_CLAY);
				is.setDurability((short) 5);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.BLUE + "Use character");
				im.setLore(Arrays.asList(ChatColor.RED + "Class: " + cc.getPlayerData().getPlayerClass().toString2(), ChatColor.RED + "Level: " + cc.getPlayerData().getLevel(), ChatColor.RED + "XP: " + cc.getPlayerData().getXP() + "/" + cc.getPlayerData().getXPRoof()));
				is.setItemMeta(im);
				inv.setItem(i * 2 + 10, is);
			}
			for(int i = 0; i < 4 - charList.size(); i++)
			{
				is.setType(Material.WOOL);
				is.setDurability((short) 15);
				im = is.getItemMeta();
				im.setLore(null);
				im.setDisplayName(ChatColor.RED + "Create character");
				is.setItemMeta(im);
				inv.setItem((charList.size()) * 2 + 10 + i * 2, is);
			}
		}
		p.openInventory(inv);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("help"))
		{
			if(sender instanceof Player)
			{
				Player p = (Player) sender;
			}
			if(!sender.isOp())
				if(sender instanceof Player)
				{
					Player p = (Player) sender;
					p.sendMessage(ChatColor.BOLD + "" + ChatColor.YELLOW + "There is nothing here yet!");
				}
		} else if(cmd.getName().equalsIgnoreCase("givecredits"))
		{
			if(!sender.isOp())
			{
				return true;
			}
			if(args.length == 2)
			{
				Player p = Bukkit.getPlayer(args[0]);
				if(p.equals(null))
				{
					sender.sendMessage(ChatColor.RED + "Invalid player.");
					return true;
				}
				try
				{
					int i = (int) Integer.parseInt(args[1]);
					CPlayer cp = PlayerDataMap.get(p.getUniqueId()).getPlayerData();
					cp.setCredits(cp.getCredits() + i);
				} catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "Inappropriate arguments.");
				}
			} else
			{
				sender.sendMessage(ChatColor.RED + "Inappropriate arguments.");
			}
		} else if(cmd.getName().equalsIgnoreCase("givexp"))
		{
			if(!sender.isOp())
			{
				return true;
			}
			if(args.length == 2)
			{
				Player p = Bukkit.getPlayer(args[0]);
				if(p.equals(null))
				{
					sender.sendMessage(ChatColor.RED + "Invalid player.");
					return true;
				}
				try
				{
					int i = (int) Integer.parseInt(args[1]);
					PlayerDataMap.get(p.getUniqueId()).getPlayerData().AddXP(i, PlayerDataMap.get(p.getUniqueId()).getPlayerData().getLevel());
				} catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "Inappropriate arguments.");
				}
			} else
			{
				sender.sendMessage(ChatColor.RED + "Inappropriate arguments.");
			}
		} else if(cmd.getName().equalsIgnoreCase("giveitem"))
		{
			if(!sender.isOp())
			{
				return true;
			}
			if(args.length == 2)
			{
				Player p = Bukkit.getPlayer(args[0]);
				if(p.equals(null))
				{
					sender.sendMessage(ChatColor.RED + "Invalid player.");
					return true;
				}
				try
				{
					int i = (int) Integer.parseInt(args[1]);
					if(i < 0 || i >= items.getItems().size())
						sender.sendMessage(ChatColor.RED + "Invalid ID.");
					else
					{
						p.getInventory().addItem(items.getItems().get(i).getItem(false));
					}
				} catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "Inappropriate arguments.");
				}
			} else
			{
				sender.sendMessage(ChatColor.RED + "Inappropriate arguments.");
			}
		} else if(cmd.getName().equalsIgnoreCase("spawnMob"))
		{
			if(!sender.isOp() || !(sender instanceof Player))
			{
				return true;
			}
			Player p = (Player) sender;
			if(args.length == 1)
			{
				try
				{
					int i = (int) Integer.parseInt(args[0]);
					boolean b = spawnCMob(i, p.getLocation());
					if(!b)
						sender.sendMessage(ChatColor.RED + "Inappropriate ID.");
				} catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "Inappropriate arguments.");
					//e.printStackTrace();
				}
			} else
			{
				sender.sendMessage(ChatColor.RED + "Inappropriate arguments.");
			}
		} else if(cmd.getName().equalsIgnoreCase("spawnNPC"))
		{
			if(!sender.isOp() || !(sender instanceof Player))
			{
				return true;
			}
			Player p = (Player) sender;
			if(args.length == 1)
			{
				try
				{
					int i = (int) Integer.parseInt(args[0]);
					boolean b = spawnCNPC(i, p.getLocation());
					if(!b)
						sender.sendMessage(ChatColor.RED + "Inappropriate ID.");
				} catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "Inappropriate arguments.");
					//e.printStackTrace();
				}
			} else
			{
				sender.sendMessage(ChatColor.RED + "Inappropriate arguments.");
			}
		} else if(cmd.getName().equalsIgnoreCase("killall"))
		{
			if(!sender.isOp())
				return false;
			int i = 0;
			for(World w : Bukkit.getWorlds())
			{
				for(org.bukkit.entity.Entity e : w.getEntities())
					if(e instanceof org.bukkit.entity.LivingEntity && !(e instanceof Player))
					{
						e.remove();
						i++;
					}
			}
			MobDataMap.clear();
			NPCDataMap.clear();
			sender.sendMessage("Cleared " + i + " entities.");
		} else if(cmd.getName().equalsIgnoreCase("test"))
		{
			if(!sender.isOp() || !(sender instanceof Player))
			{
				return true;
			}
				Player p = (Player) sender;
		}
		return true;
	}
}