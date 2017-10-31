package quake.thekian.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import me.kian.particles.ParticlesMain;
import quake.thekian.main.WorldData.WorldType;
import quake.thekian.util.ZipUtils;
import quake.thekian.weapons.ArmorStandProjectile;
import quake.thekian.weapons.AttackData.ProjectileData;
import quake.thekian.weapons.Weapon;
import quake.thekian.weapons.WepData;
import quake.thekian.weapons.WepData.WeaponType;
import quake.thekian.weapons.WepProcessor;
import thekian.nms.protocol.Packets;
import thekian.nms.protocol.Particles;
import thekian.nms.protocol.Titles;


public class QuakeMain extends JavaPlugin implements Listener
{
	private Plugin plugin;
	ParticlesMain partMain;
	static boolean GameEnabled, GameActive, GameStarted, CountdownStarted, PvPEnabled, endTrigger;
	private HashMap<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>();
	private HashMap<Projectile, ProjectileData> projectileData = new HashMap<Projectile, ProjectileData>();
	private ArrayList<ArmorStandProjectile> armorStandData = new ArrayList<ArmorStandProjectile>();
	private ArrayList<UUID> spectators = new ArrayList<UUID>();
	private List<WorldData> worldData = new ArrayList<WorldData>();
	private HashMap<ArmorStand, WeaponPickup> weaponLocations = new HashMap<ArmorStand, WeaponPickup>();
	private HashMap<ArmorStand, AmmoPickup> ammoLocations = new HashMap<ArmorStand, AmmoPickup>();
	private HashMap<ArmorStand, HealthPickup> healthLocations = new HashMap<ArmorStand, HealthPickup>();
	private static HashMap<WeaponType, Integer> weaponIndices = new HashMap<WeaponType, Integer>();
	private static HashMap<WeaponType, Material> ammoMaterials = new HashMap<WeaponType, Material>();
	private static HashMap<WeaponType, AmmoPickup> ammoValues = new HashMap<WeaponType, AmmoPickup>();
	final int DEFAULT_START = 60;
	final int FILLED_START = 20;
	final int[] ALERT_TIMES = {30, 20, 10, 5, 4, 3, 2, 1};
	final int FILLED_PLAYERS = 1;
	final int MIN_PLAYERS = 1;
	final int FRAGS_TO_END = 10;
	final int MIN_PLAYERS_END = 0;
	private String[] places = {null, null, null};
	//final String[] TIPS = {ChatColor.GRAY + "Warlords can swap out their weapons (Default key: F) to have either better defense or damage.",
	//		ChatColor.GRAY + "Clerics have a regeneration ability that increases in-combat longevity.",
	//		ChatColor.GRAY + "Chevaliers can activate " + Messages.CHEVALIER_ABILITY_NAME_1.getValue() + ChatColor.GRAY + ", which grants temporary 50% damage resistance.",
	//		ChatColor.GRAY + "Barbarians can use " + Messages.BARBARIAN_ABILITY_NAME_1.getValue() + ChatColor.GRAY + " to escape combat quickly and do heavy damage.",
	//		ChatColor.GRAY + "Warlords can, depending on the weapon in their main hand, activate temporary defensive or damage boosts."};
	public static String serverPath, pluginPath;
	private WorldData currentWorldData;
	private World currentWorld;
	private int currentData;
	
	BukkitRunnable gameEndRunnable = new BukkitRunnable(){
		
		public void run()
		{
			resetPluginInstance();
			for(Player p : Bukkit.getOnlinePlayers())
				setPlayerBase(p, false, 0.2F, false, false, false);
			startQuake();
		}
		
	};
	
	BukkitRunnable freezePlayerRunnable = new BukkitRunnable(){
		
		public void run()
		{
			for(PlayerData pd : playerData.values())
			{
				pd.teleportToStartLoc();
			}
		}
		
	};
	
	@Override
	public void onEnable()
	{
		endTrigger = false;
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		plugin = this;
		WepData.initialize();
		serverPath = Bukkit.getServer().getWorldContainer().getAbsolutePath();
		for(int i = serverPath.length() - 1; i >= 0; i--)
		{
			if(serverPath.charAt(i) == File.separatorChar)
			{
				serverPath = serverPath.substring(0, i + 1);
				break;
			}
		}
		pluginPath = serverPath + "plugins" + File.separator;
		if(!(new File(pluginPath + "QuakeWorldData")).exists())
		{
			new File(pluginPath + "QuakeWorldData").mkdirs();
		}
		if(!(new File(serverPath + "spawn")).exists())
		{
			new File(serverPath + "spawn").mkdirs();
		}
		Bukkit.getLogger().info("Server path: " + serverPath);
		Bukkit.getLogger().info("Plugin path: " + pluginPath);
		Bukkit.getLogger().info("Attempting to load " + pluginPath + "QuakeWorldData" + File.separator + "spawn.dat");
		worldData.add(new WorldData(new File(pluginPath + "QuakeWorldData" + File.separator + "spawn.dat"), true));
		for(File f : (new File(pluginPath + "QuakeWorldData")).listFiles())
		{
			if(!f.getName().equalsIgnoreCase("spawn.dat"))
			{
				Bukkit.getLogger().info("Attempting to load " + f.getPath());
				worldData.add(new WorldData(f, false));
			}
		}
		//Buffer handler
		new BukkitRunnable()
		{
			public void run()
			{
				if(GameActive)
				{
					for(Projectile proj : WepProcessor.retrieveProjectileDataBuffer().keySet())
						projectileData.put(proj, WepProcessor.retrieveProjectileDataBuffer().get(proj));
					for(ArmorStandProjectile proj : WepProcessor.retrieveArmorStandDataBuffer())
						armorStandData.add(proj);
					WepProcessor.purgeLists();
				}
			}
		}.runTaskTimer(plugin, 0, 2);
		
		//Armor stand projectiles
		new BukkitRunnable()
		{
			
			public void run()
			{
				if(GameActive)
				{
					ArrayList<LivingEntity> affected = new ArrayList<LivingEntity>();
					for(UUID uid : playerData.keySet())
						if(!spectators.contains(uid))
							affected.add(Bukkit.getPlayer(uid));
					ArmorStandProcessor.process(armorStandData, affected, spectators);
				}
			}
			
		}.runTaskTimer(plugin, 0, 1);
		
		//HUD
		new BukkitRunnable()
		{
			public void run()
			{
				if(GameActive && GameStarted)
				{
					for(Player p : Bukkit.getOnlinePlayers())
					{
						if(playerData.keySet().contains(p.getUniqueId()) && !spectators.contains(p.getUniqueId()))
						{
							String hlt = String.valueOf((int) p.getHealth());
							String wep = "Nothing selected"; 
							if(playerData.get(p.getUniqueId()).getWeapon(p.getInventory().getHeldItemSlot()) != null)
								wep = String.valueOf(playerData.get(p.getUniqueId()).getAmmo(p.getInventory().getHeldItemSlot()));
							String s = (ChatColor.RED + "Health: " + ChatColor.YELLOW + hlt + "   " + ChatColor.RED + "Ammo: " + ChatColor.YELLOW + wep);
							//String s = (ChatColor.RED + "Test" + ChatColor.YELLOW + " Please work");
							Packets.sendPacket(p, Titles.createHotbarPacket(s));
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 1);
		
		//Pickups
		new BukkitRunnable()
		{
			public void run()
			{
				if(GameActive && GameStarted)
				{
					for(Player p : Bukkit.getOnlinePlayers())
					{
						if(playerData.containsKey(p.getUniqueId()) && playerData.get(p.getUniqueId()).getAlive() && !spectators.contains(p.getUniqueId()))
						{
							Location loc = p.getLocation();
							for(ArmorStand as : ammoLocations.keySet())
							{
								Location loc2 = new Location(as.getWorld(), as.getLocation().getX(), as.getLocation().getY() + 0.5, as.getLocation().getZ());
								if(loc2.distance(loc) < 1 && ammoLocations.get(as).getCanBeUsed())
								{
									playerData.get(p.getUniqueId()).addAmmo(ammoLocations.get(as).getAmmoAmount(), weaponIndices.get(ammoLocations.get(as).getAmmoType()).intValue());
									ammoLocations.get(as).setUsed(true);
									as.setHelmet(new ItemStack(Material.AIR));
									new BukkitRunnable(){
										
										public void run()
										{
											if(GameActive && ammoLocations.containsKey(as))
											{
												ammoLocations.get(as).setUsed(false);
												as.setHelmet(new ItemStack(ammoMaterials.get(ammoLocations.get(as).getAmmoType())));
											}
										}
										
									}.runTaskLater(plugin, 20 * ammoLocations.get(as).getRechargeTime());
								}
							}
							for(ArmorStand as : weaponLocations.keySet())
							{
								Location loc2 = new Location(as.getWorld(), as.getLocation().getX(), as.getLocation().getY() + 0.5, as.getLocation().getZ());
								if(loc2.distance(loc) < 1 && weaponLocations.get(as).getCanBeUsed())
								{
									PlayerData pd = playerData.get(p.getUniqueId());
									int index = weaponIndices.get(weaponLocations.get(as).getWeaponType()).intValue();
									if(pd.getAmmo(index) >= weaponLocations.get(as).getAmmoAmount())
										pd.addAmmo(1, index);
									else
										pd.setAmmo(weaponLocations.get(as).getAmmoAmount(), index);
									if(pd.getWeapon(index) == null)
									{
										Weapon weapon = WepData.getWeaponInstance(weaponLocations.get(as).getWeaponType()); 
										pd.setWeapon(weapon, index);
										p.getInventory().setItem(index, weapon.getItem());
									}
									weaponLocations.get(as).setUsed(true);
									as.setHelmet(new ItemStack(Material.AIR));
									new BukkitRunnable(){
										
										public void run()
										{
											if(GameActive && weaponLocations.containsKey(as))
											{
												weaponLocations.get(as).setUsed(false);
												as.setHelmet(WepData.getWeaponInstance(weaponLocations.get(as).getWeaponType()).getItem());
											}
										}
										
									}.runTaskLater(plugin, 20 * weaponLocations.get(as).getRechargeTime());
								}
							}
							for(ArmorStand as : healthLocations.keySet())
							{
								Location loc2 = new Location(as.getWorld(), as.getLocation().getX(), as.getLocation().getY() + 0.5, as.getLocation().getZ());
								if(loc2.distance(loc) < 1 && healthLocations.get(as).getCanBeUsed() && p.getHealth() < p.getMaxHealth())
								{	
									if(p.getHealth() + healthLocations.get(as).getHealthAmount() > p.getMaxHealth())
										p.setHealth(p.getMaxHealth());
									else
										p.setHealth(Math.max(p.getHealth() + healthLocations.get(as).getHealthAmount(), 1));
									as.setHelmet(new ItemStack(Material.AIR));
									new BukkitRunnable(){
										
										public void run()
										{
											if(GameActive && weaponLocations.containsKey(as))
											{
												healthLocations.get(as).setUsed(false);
												as.setHelmet(new ItemStack(Material.APPLE));
											}
										}
										
									}.runTaskLater(plugin, 20 * healthLocations.get(as).getRechargeTime());
								}
							}
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0, 5);
		
		//Pickup rotation
		new BukkitRunnable(){
			
			public void run()
			{
				if(GameActive && GameStarted)
				{
					for(ArmorStand as : ammoLocations.keySet())
					{
						as.setHeadPose(as.getHeadPose().add(0, 0.1, 0));
					}
					for(ArmorStand as : weaponLocations.keySet())
					{
						as.setHeadPose(as.getHeadPose().add(0, 0.1, 0));	
					}
					for(ArmorStand as : healthLocations.keySet())
					{
						as.setHeadPose(as.getHeadPose().add(0, 0.1, 0));	
					}
				}
			}
			
		}.runTaskTimer(plugin, 0, 1);
		
		//Cooldown code
		new BukkitRunnable(){
			
			public void run()
			{
				if(GameActive)
					for(PlayerData pd : playerData.values())
					{
						if(!pd.getRespawning())
							for(int i = 0; i < 9; i++)
							{
								if(pd.getWeapon(i) != null)
								{
									Weapon w = pd.getWeapon(i);
									w.tickCooldown();
								}
							}
					}
			}
			
		}.runTaskTimer(plugin, 0, 1);
		
		//Game start code
		new BukkitRunnable(){
			
			public void run()
			{
				if(GameEnabled && !GameActive && !GameStarted && !CountdownStarted)
					if(Bukkit.getOnlinePlayers().size() > MIN_PLAYERS)
						if(Bukkit.getOnlinePlayers().size() > FILLED_PLAYERS)
							startCountdown(FILLED_START);
						else
							startCountdown(DEFAULT_START);
			}
			
		}.runTaskTimer(plugin, 0, 20);
		
		new BukkitRunnable(){
			
			public void run()
			{
				if(GameActive)
					if(getAlivePlayers() <= MIN_PLAYERS_END && !endTrigger)
					{
						endTrigger = true;
						broadcastEndgame();
						if(getAlivePlayers() == 1)
							for(PlayerData pd : playerData.values())
								if(pd.getAlive())
								{
									places[0] = pd.getPlayer().getName();
									setSpectator(pd.getPlayer(), true);
								}
						gameEndRunnable.runTaskLater(plugin, 100);
					}
			}
			
		}.runTaskTimer(plugin, 0, 40);
		for(int i = 0; i < worldData.size(); i++)
			loadWorld(i);
	}
	
	public void setWorld(String name, int index)
	{
		currentWorld = Bukkit.getServer().getWorld(name);
		currentWorldData = worldData.get(index);
		System.out.println(currentWorldData.getWorldType());
		if(currentWorldData.getWorldType().equals(WorldType.SPAWN))
		{
			CoordSet cs = currentWorldData.getStartLocations().get(0);
			for(Player p : Bukkit.getOnlinePlayers())
			{
				p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
				setPlayerBase(p, false, 0.2F, false, true, false);
				new BukkitRunnable(){ //Extra delayed teleport to teleport player to the correct world location 
					
					public void run()
					{
						p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
					}
					
				}.runTaskLater(plugin, 2);
			}
		} else if(currentWorldData.getWorldType().equals(WorldType.GAMEWORLD))
		{
			System.out.println("Gameworld start.");
			for(Entity e : currentWorld.getEntities())
				if(e instanceof ArmorStand)
					e.remove();
			for(CoordSet cs : currentWorldData.getAmmoSpawns().keySet())
			{
				Location loc = new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ());
				if(!loc.getChunk().isLoaded())
					loc.getChunk().load();
				ArmorStand armorStand = (ArmorStand) currentWorld.spawnEntity(loc, EntityType.ARMOR_STAND);
				armorStand.setCollidable(false);
				armorStand.setGravity(false);
				armorStand.setVisible(false);
				armorStand.setSmall(true);
				armorStand.setSilent(true);
				armorStand.setCanPickupItems(false);
				armorStand.setInvulnerable(true);
				armorStand.setHelmet(new ItemStack(ammoMaterials.get(currentWorldData.getAmmoSpawns().get(cs))));
				ammoLocations.put(armorStand, ammoValues.get(currentWorldData.getAmmoSpawns().get(cs)).createCopy());
			}
			for(CoordSet cs : currentWorldData.getWeaponSpawns().keySet())
			{
				Location loc = new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ());
				if(!loc.getChunk().isLoaded())
					loc.getChunk().load();
				ArmorStand armorStand = (ArmorStand) currentWorld.spawnEntity(loc, EntityType.ARMOR_STAND);
				armorStand.setCollidable(false);
				armorStand.setGravity(false);
				armorStand.setVisible(false);
				armorStand.setSmall(true);
				armorStand.setSilent(true);
				armorStand.setCanPickupItems(false);
				armorStand.setInvulnerable(true);
				WeaponType wt = currentWorldData.getWeaponSpawns().get(cs);
				armorStand.setHelmet(WepData.getWeaponInstance(wt).getItem());
				weaponLocations.put(armorStand, new WeaponPickup(wt, ammoValues.get(wt).getAmmoAmount(), ammoValues.get(wt).getRechargeTime()));
			}
			for(CoordSet cs : currentWorldData.getHealthSpawns().keySet())
			{
				Location loc = new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ());
				if(!loc.getChunk().isLoaded())
					loc.getChunk().load();
				ArmorStand armorStand = (ArmorStand) currentWorld.spawnEntity(loc, EntityType.ARMOR_STAND);
				armorStand.setCollidable(false);
				armorStand.setGravity(false);
				armorStand.setVisible(false);
				armorStand.setSmall(true);
				armorStand.setSilent(true);
				armorStand.setCanPickupItems(false);
				armorStand.setInvulnerable(true);
				armorStand.setHelmet(new ItemStack(Material.APPLE));
				healthLocations.put(armorStand, new HealthPickup(currentWorldData.getHealthSpawns().get(cs).intValue(), 30));
			}
			int[] usedLocs = new int[currentWorldData.getStartLocations().size()];
			for(int i = 0; i < usedLocs.length; i++)
				usedLocs[i] = -1;
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(playerData.keySet().contains(p.getUniqueId()))
				{
					if(usedLocs[currentWorldData.getStartLocations().size() - 1] > -1)
					{
						for(int i = 0; i < usedLocs.length; i++)
							usedLocs[i] = -1;
					}
						int i = getSemiRandomInt(usedLocs, 0, currentWorldData.getStartLocations().size() - 1);
						CoordSet cs = currentWorldData.getStartLocations().get(i);
						p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
						playerData.get(p.getUniqueId()).setStartLocation(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()));
						new BukkitRunnable(){ //Extra delayed teleport to teleport player to the correct world location 
							
							public void run()
							{
								p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
								System.out.println("Teleport 2 to: " + currentWorld.getName());
							}
							
						}.runTaskLater(plugin, 2);
						for(int i2 = 0; i2 < usedLocs.length; i2++)
						{
							if(usedLocs[i2] == -1)
							{
								usedLocs[i2] = i;
								break;
							}
						}
				}
			}
		}
	}
	
	private int getSemiRandomInt(int[] forbidden, int min, int max)
	{
		int i = (int) Math.floor(Math.random() * (max + 1)) + min;
		boolean b = false;
		for(int integer : forbidden)
		{
			if(i == integer)
				b = true;
		}
		if(b)
			return getSemiRandomInt(forbidden, min, max);
		return i;
	}
	
	public void loadWorld(int i)
	{
		try
		{
			if(worldData.get(i).getWorldType().equals(WorldType.SPAWN))
			{
				if(Bukkit.getServer().getWorld("spawn") != null)
					Bukkit.getServer().unloadWorld("spawn", true);
				File f = new File(serverPath + "spawn");
				for(File sub : f.listFiles())
					sub.delete();
				ZipUtils.unzip(serverPath + worldData.get(i).getFileName(), serverPath + "spawn");
				Bukkit.getServer().createWorld(new WorldCreator("spawn"));
			} else if(worldData.get(i).getWorldType().equals(WorldType.GAMEWORLD))
			{
				if(!(new File(serverPath + worldData.get(i).getWorldName())).exists())
				{
					new File(serverPath + worldData.get(i).getWorldName()).mkdirs();
				}
				if(Bukkit.getServer().getWorld(worldData.get(i).getWorldName()) != null)
					Bukkit.getServer().unloadWorld(worldData.get(i).getWorldName(), true);
				File f = new File(serverPath + worldData.get(i).getWorldName());
				for(File sub : f.listFiles())
					sub.delete();
				ZipUtils.unzip(serverPath + worldData.get(i).getFileName(), serverPath + worldData.get(i).getWorldName());
				Bukkit.getServer().createWorld(new WorldCreator(worldData.get(i).getWorldName()));
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		if(GameActive && e.getDamage() > 0 && e.getDamage() < 9000)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		if(GameActive && playerData.keySet().contains(e.getEntity().getUniqueId()))
		{
			Player p = e.getEntity();
			//"Die"
			e.setDeathMessage(null);
			e.setDroppedExp(0);
			e.setKeepInventory(true);
			p.getInventory().clear();
			playerData.get(p.getUniqueId()).setAlive(false);
			setSpectator(p, true);
			p.setHealth(20);
			//Death message
			String m = "";
			if(p.getKiller() != null && !p.getKiller().equals(p))
			{
				m = Messages.QUAKE.getValue() + ChatColor.GREEN + p.getName() + ChatColor.GRAY + " was gibbed by " + ChatColor.GREEN + p.getKiller().getName() + ChatColor.GRAY + ".";
				if(playerData.keySet().contains(p.getKiller().getUniqueId()))
					playerData.get(p.getKiller().getUniqueId()).addKill();
			} else if(p.getKiller() != null)
			{
				m = Messages.QUAKE.getValue() + ChatColor.GREEN + p.getName() + ChatColor.GRAY + " gibbed himself.";
			}
			else
				m = Messages.QUAKE.getValue() + ChatColor.GREEN + p.getName() + ChatColor.GRAY + " died.";
			for(Player p2 : Bukkit.getOnlinePlayers())
			{
				p2.sendMessage(m);
			}
			p.sendMessage(Messages.RESPAWN.getValue());
			PlayerData deadP = playerData.get(p.getUniqueId());
			new BukkitRunnable(){
				
				public void run()
				{
					CoordSet cs = currentWorldData.getStartLocations().get((int) (Math.random() * currentWorldData.getStartLocations().size()));
					if(GameActive)
					{
						p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()));
						deadP.setRespawning(false);
						setPlayerBase(p, false, 0.35F, true, false, true);
						Weapon gaunt = WepData.getWeaponInstance(WeaponType.GAUNTLET); 
						deadP.setWeapon(gaunt, 0);
						p.getInventory().setItem(0, gaunt.getItem());
						Weapon macgun = WepData.getWeaponInstance(WeaponType.MACHINEGUN);
						deadP.setWeapon(macgun, 1);
						p.getInventory().setItem(1, macgun.getItem());
						deadP.setAmmo(50, 1);
						/*for(UUID uid : spectators)
						{
							System.out.println("Trying to remove");
							if(uid.equals(p.getUniqueId()))
							{
								System.out.println("UUID equal, removing PLEASE");
								UUID temp = spectators.remove(spectators.indexOf(uid));
								System.out.println("DEBUG: " + temp.equals(uid));
								System.out.println("DEBUG: " + spectators.contains(p.getUniqueId()));
							}
						}*/
						boolean b = spectators.remove(p.getUniqueId());
						System.out.println("DEBUG: " + b);
						System.out.println("DEBUG 2: " + spectators.contains(p.getUniqueId()));
						spectators.remove(spectators.indexOf(p.getUniqueId()));
						System.out.println("DEBUG 3: " + spectators.contains(p.getUniqueId()));
						ParticlesMain.removeImmunePlayer(p);
						for(Player p2 : Bukkit.getOnlinePlayers())
						{
							p2.showPlayer(p);
						}
					}
				}
				
			}.runTaskLater(plugin, 100);
			playerData.get(p.getUniqueId()).setWeapon(WepData.getWeaponInstance(WeaponType.GAUNTLET), 0);
			playerData.get(p.getUniqueId()).setWeapon(WepData.getWeaponInstance(WeaponType.MACHINEGUN), 1);
			playerData.get(p.getUniqueId()).setWeapon(null, 2);
			playerData.get(p.getUniqueId()).setWeapon(null, 3);
			playerData.get(p.getUniqueId()).setWeapon(null, 4);
			playerData.get(p.getUniqueId()).setWeapon(null, 5);
			playerData.get(p.getUniqueId()).setWeapon(null, 6);
			playerData.get(p.getUniqueId()).setWeapon(null, 7);
			playerData.get(p.getUniqueId()).setWeapon(null, 8);
			
			playerData.get(p.getUniqueId()).setAmmo(0, 0);
			playerData.get(p.getUniqueId()).setAmmo(100, 1);
			playerData.get(p.getUniqueId()).setAmmo(0, 1);
			playerData.get(p.getUniqueId()).setAmmo(0, 3);
			playerData.get(p.getUniqueId()).setAmmo(0, 4);
			playerData.get(p.getUniqueId()).setAmmo(0, 5);
			playerData.get(p.getUniqueId()).setAmmo(0, 6);
			playerData.get(p.getUniqueId()).setAmmo(0, 7);
			playerData.get(p.getUniqueId()).setAmmo(0, 8);
			if(p.getKiller() != null && playerData.get(p.getKiller().getUniqueId()).getKills() >= FRAGS_TO_END)
			{
				endTrigger = true;
				for(PlayerData pd : playerData.values())
				{
					if(places[0] == null || places[0] != null && pd.getKills() > playerData.get(Bukkit.getPlayer(places[0])).getKills())
					{
						places[2] = places[1];
						places[1] = places[0];
						places[0] = pd.getPlayer().getName();
					} else if(places[1] == null || places[1] != null && pd.getKills() > playerData.get(Bukkit.getPlayer(places[1])).getKills())
					{
						places[2] = places[1];
						places[1] = pd.getPlayer().getName();
					} else if(places[2] == null || places[2] != null && pd.getKills() > playerData.get(Bukkit.getPlayer(places[2])).getKills())
						places[2] = pd.getPlayer().getName();				
				}
				broadcastEndgame();
				if(getAlivePlayers() == 1)
					for(PlayerData pd : playerData.values())
						if(pd.getAlive())
						{
							places[0] = pd.getPlayer().getName();
							setSpectator(pd.getPlayer(), true);
						}
				gameEndRunnable.runTaskLater(plugin, 100);	
			}
			new BukkitRunnable(){
				
				public void run()
				{
					if(GameActive && Bukkit.getOnlinePlayers().contains(p))
					{
						playerData.get(p.getUniqueId()).setAlive(true);
						CoordSet cs = currentWorldData.getStartLocations().get((int) Math.floor((Math.random() * currentWorldData.getStartLocations().size())));
						p.teleport(new Location(p.getWorld(), cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
						p.getInventory().setItem(0, playerData.get(p.getUniqueId()).getWeapon(0).getItem());
						p.getInventory().setItem(1, playerData.get(p.getUniqueId()).getWeapon(1).getItem());
					}
				}
				
			}.runTaskLater(plugin, 100);
			if(getAlivePlayers() <= 1 && !endTrigger)
			{
				endTrigger = true;
				broadcastEndgame();
				if(getAlivePlayers() == 1)
					for(PlayerData pd : playerData.values())
						if(pd.getAlive())
						{
							places[0] = pd.getPlayer().getName();
							setSpectator(pd.getPlayer(), true);
						}
				gameEndRunnable.runTaskLater(plugin, 100);
			}
			//Set spectator
			setSpectator(p, true);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		if(playerData.containsKey(e.getPlayer().getUniqueId()))
			playerData.get(e.getPlayer().getUniqueId()).setAlive(false);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if(GameEnabled && !GameActive && !GameStarted && !CountdownStarted)
		{
			CoordSet cs = currentWorldData.getStartLocations().get(0);
			e.getPlayer().teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()));
		} else if(GameEnabled && GameActive)
		{
			setSpectator(e.getPlayer(), false);
		}
		for(UUID uid : spectators)
			e.getPlayer().hidePlayer(Bukkit.getPlayer(uid));
	}
	
	@EventHandler
	public void onSprintChange(PlayerToggleSprintEvent e)
	{
		if(GameActive && !GameStarted)
		{
			e.setCancelled(true);
			e.getPlayer().setSprinting(false);
		}
	}
	
	@EventHandler
	public void onRegen(EntityRegainHealthEvent e)
	{
		RegainReason rr = e.getRegainReason();
		if(rr.equals(RegainReason.SATIATED) || rr.equals(RegainReason.EATING))
			e.setCancelled(true);
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent e)
	{
		if(GameEnabled)
		{
			if(e.getEntity() instanceof Player)
			{
				Player p = (Player) e.getEntity();
				new BukkitRunnable(){
					
					public void run()
					{
						p.setFoodLevel(20);
					}
					
				}.runTaskLater(plugin, 1);
			}
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		if(GameEnabled)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if(GameActive && GameStarted)
			if(playerData.keySet().contains(e.getPlayer().getUniqueId()))
				if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				{
					PlayerData pd = playerData.get(e.getPlayer().getUniqueId());
					Weapon weapon = pd.getWeapon(e.getPlayer().getInventory().getHeldItemSlot());
					if(weapon != null && weapon.canFire() && (pd.getAmmo(e.getPlayer().getInventory().getHeldItemSlot()) > 0 || weapon.getWeaponType().equals(WeaponType.GAUNTLET)))
					{
						weapon.fireCooldown();
						pd.addAmmo(-1, e.getPlayer().getInventory().getHeldItemSlot());
						WepProcessor.processWeaponUsage(pd.getWeapon(e.getPlayer().getInventory().getHeldItemSlot()), e.getPlayer(), getLivingPlayers());
					}
				} else
				{
					e.setCancelled(true);
				}
			else
				e.setCancelled(true);
		else if(GameEnabled)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
		if(projectileData.keySet().contains(e.getEntity()))
		{
			if(projectileData.get(e.getEntity()).getDoesSplash())
			{
				ProjectileData pd = projectileData.get(e.getEntity());
				double range = Math.max(Math.max(pd.getSplashData(0), pd.getSplashData(1)), pd.getSplashData(2));
				for(Entity ent : e.getEntity().getNearbyEntities(range, range, range))
				{
					if(ent instanceof LivingEntity)
					{
						LivingEntity le2 = (LivingEntity) ent;
						Location loc = e.getEntity().getLocation();
						if(customDistance(loc, le2) <= pd.getSplashData(0))
						{
							le2.setHealth(le2.getHealth() - (pd.getDamageData(0)));
							if(e.getEntity().getShooter() instanceof Player)
								le2.damage(0,  (Player) e.getEntity().getShooter());
						} else if(customDistance(loc, le2) <= pd.getSplashData(1))
						{
							le2.setHealth(le2.getHealth() - (pd.getDamageData(0) / 0.5));
							if(e.getEntity().getShooter() instanceof Player)
								le2.damage(0,  (Player) e.getEntity().getShooter());
						} else if(customDistance(loc, le2) <= pd.getSplashData(2))
						{
							le2.setHealth(le2.getHealth() - (pd.getDamageData(0) / 0.25));
							if(e.getEntity().getShooter() instanceof Player)
								le2.damage(0,  (Player) e.getEntity().getShooter());
						}
					}
				}
			} else
				for(Entity ent : e.getEntity().getNearbyEntities(0.6, 0.6, 0.6))
				{
					if(ent instanceof LivingEntity)
					{
						ProjectileData pd = projectileData.get(e.getEntity());
						LivingEntity le = (LivingEntity) ent;
						le.setHealth(le.getHealth() - pd.getDamageData(0));
						if(pd.getDamageData(1) > 0)
							le.setFireTicks((int) pd.getDamageData(1));
						if(pd.getDamageData(2) > 0)
						{
							Vector v = e.getEntity().getLocation().getDirection().normalize().multiply(pd.getDamageData(2));
							Vector v2 = le.getVelocity();
							v2.setX(v2.getX() + v.getX());
							v2.setY(v2.getY() + v.getY());
							v2.setZ(v2.getZ() + v.getZ());
							le.setVelocity(v2);
						}
					}
				}
		}
	}
	
	private static double customDistance(Location loc, LivingEntity le)
	{
		if(le != null)
		{
			return loc.distance(new Location(le.getWorld(), le.getLocation().getX(), (le.getEyeHeight() - le.getLocation().getY()) / 2 + 0.2, le.getLocation().getZ()));
		}
		return 0;
	}
	
	public List<LivingEntity> getLivingPlayers()
	{
		ArrayList<LivingEntity> list = new ArrayList<LivingEntity>();
		for(Player p : Bukkit.getOnlinePlayers())
		{
			System.out.println(p.getName() + ": In player data: " + playerData.keySet().contains(p.getUniqueId()) + ", is spectator: " + spectators.contains(p.getUniqueId()));
			if(playerData.keySet().contains(p.getUniqueId()) && !spectators.contains(p.getUniqueId()))
				list.add(p);
		}
		return list;
	}
	
	public void gameCountdown(int i)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(i > 0)
				p.sendMessage(Messages.COUNTDOWN.getValue() + ChatColor.GREEN + i + Messages.COOLDOWN_3.getValue());
			else
			{
				p.sendMessage(Messages.QUAKE_START.getValue());
				if(playerData.keySet().contains(p.getUniqueId()))
				{
					p.setInvulnerable(false);
					PlayerData pd = playerData.get(p.getUniqueId());
					unfreezePlayer(p);
					freezePlayerRunnable.cancel();
					Weapon gaunt = WepData.getWeaponInstance(WeaponType.GAUNTLET); 
					pd.setWeapon(gaunt, 0);
					p.getInventory().setItem(0, gaunt.getItem());
					Weapon macgun = WepData.getWeaponInstance(WeaponType.MACHINEGUN);
					pd.setWeapon(macgun, 1);
					p.getInventory().setItem(1, macgun.getItem());
					pd.setAmmo(50, 1);
				}
				GameStarted = true;
				CountdownStarted = false;
				ParticlesMain.startProcessing();
			}
		}
		new BukkitRunnable(){
			
			public void run()
			{
				if(i > 0)
					gameCountdown(i - 1);
			}
			
		}.runTaskLater(plugin, 20);
	}
	
	public void startCountdown(int i)
	{
		CountdownStarted = true;
		if(Bukkit.getOnlinePlayers().size() < MIN_PLAYERS)
		{
			for(Player p : Bukkit.getOnlinePlayers())
				p.sendMessage(Messages.QUAKE.getValue() + ChatColor.GRAY + "Countdown cancelled. Too few players!");
			return;
		}
		if(i <= 0)
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				p.closeInventory();
				p.sendMessage(Messages.QUAKE_START.getValue());
				playerData.put(p.getUniqueId(), new PlayerData(p, true));
			}
			System.out.println("Setting game world.");
			GameActive = true;
			setWorld(worldData.get(currentData).getWorldName(), currentData);
			for(Player p : Bukkit.getOnlinePlayers())
				if(playerData.keySet().contains(p.getUniqueId()))
				{
					freezePlayer(p);
				}
			freezePlayerRunnable.runTaskTimer(plugin, 0, 3);
			gameCountdown(10);
		}
		for(int i2 : ALERT_TIMES)
			if(i2 == i)
				for(Player p : Bukkit.getOnlinePlayers())
					p.sendMessage(Messages.COUNTDOWN_2.getValue() + ChatColor.GREEN + i + Messages.COOLDOWN_3.getValue());
		if(Bukkit.getOnlinePlayers().size() >= FILLED_PLAYERS && i > FILLED_START)
			startCountdown(FILLED_START);
		new BukkitRunnable(){
			
			public void run()
			{
				if(i > 0)
					startCountdown(i - 1);
			}
			
		}.runTaskLater(plugin, 20);
	}
	
	public boolean getGameEnabled()
	{
		return GameEnabled;
	}
	
	public void setPlayerBase(Player player, boolean canFly, float speed, boolean gameHealth, boolean invulnerable, boolean jumpboost)
	{
		player.setWalkSpeed(speed);
		player.setFoodLevel(20);
		player.setInvulnerable(invulnerable);
		player.removePotionEffect(PotionEffectType.JUMP);
		if(jumpboost)
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 32767, 1, true, false)); //Gives the desired jump boost for the game
		}
		if(gameHealth)
		{
			player.setMaxHealth(100);
			player.setHealth(100);
		} else
		{
			player.setMaxHealth(20);
			player.setHealth(20);	
		}
		player.setCanPickupItems(false);
		player.setAllowFlight(canFly);
		player.getInventory().clear();
	}
	
	private void freezePlayer(Player p)
	{
		p.setWalkSpeed(0.0F); //Disables walking
		p.removePotionEffect(PotionEffectType.JUMP);
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 32767, 128, true, false)); //Disables jumping
	}
	
	private void unfreezePlayer(Player p)
	{
		//p.setWalkSpeed(0.5F); //Faster run speed
		//p.removePotionEffect(PotionEffectType.JUMP);
		//p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 32767, 2, true, false)); //Gives the desired jump boost for the game
		setPlayerBase(p, false, 0.35F, true, false, true);
	}
	
	/***
	 * Completely freezes players who are on the ground.
	 * @param players - List of players to be frozen.
	 */
	public void freezePlayers(List<Player> players)
	{
		for(Player p : players)
		{
			freezePlayer(p);
		}
	}
	
	/***
	 * Unfreezes players frozen by freezePlayers().
	 * @param players - List of players to be unfrozen.
	 */
	public void unfreezePlayers(List<Player> players)
	{
		for(Player p : players)
		{
			unfreezePlayer(p);
		}
	}
	
	public void setSpectator(Player p, boolean mess)
	{
		p.setCollidable(false);
		p.setInvulnerable(true);
		p.removePotionEffect(PotionEffectType.JUMP);
		setPlayerBase(p, true, 0.2F, false, true, false);
		if(mess)
			p.sendMessage(Messages.SPECTATOR.getValue());
		spectators.add(p.getUniqueId());
		p.setGameMode(GameMode.SURVIVAL);
		for(Player p2 : Bukkit.getOnlinePlayers())
			p2.hidePlayer(p);
		if(GameActive)
		{
			CoordSet cs = currentWorldData.getSpectatorStartLocation();
			if(cs == null)
				p.teleport(new Location(currentWorld, 0, 10, 0));
			else
				p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()));
		}
		ParticlesMain.addImmunePlayer(p);
	}
	
	public int getAlivePlayers()
	{
		int i = 0;
		for(PlayerData pd : playerData.values())
			i++;
		return i;
	}
	
	/***
	 * Initalizes the player for the game. Freezes him and sets his proper statistics.
	 * @param player - Player to be initialized.
	 */
	public void initPlayer(Player player)
	{
		if(spectators.contains(player.getUniqueId()))
		{
			setSpectator(player, false);
			return;
		}
		setPlayerBase(player, false, 0.35F, true, false, false);
		freezePlayer(player);
	}
	
	public void resetPluginInstance()
	{
		places[0] = null;
		places[1] = null;
		places[2] = null;
		for(UUID uid : spectators)
			ParticlesMain.removeImmunePlayer(Bukkit.getPlayer(uid));
		spectators.clear();
		playerData.clear();
		for(ArmorStand as : ammoLocations.keySet())
			as.remove();
		for(ArmorStand as : weaponLocations.keySet())
			as.remove();
		for(ArmorStand as : healthLocations.keySet())
			as.remove();
		ammoLocations.clear();
		weaponLocations.clear();
		healthLocations.clear();
		ParticlesMain.stopProcessing();
		GameActive = false;
		GameStarted = false;
		endTrigger = false;
		for(Player p : Bukkit.getOnlinePlayers())
		{
			setPlayerBase(p, false, 0.2F, false, true, false);
			for(Player p2 : Bukkit.getOnlinePlayers())
				p.showPlayer(p2);
		}
	}
	
	public void sendPlayerStats(Player p)
	{
		if(playerData.keySet().contains(p.getUniqueId()))
		{
			p.sendMessage(Messages.BORDER.getValue());
			p.sendMessage(" ");
			p.sendMessage(ChatColor.YELLOW + "        Your kills:" + ChatColor.GRAY + playerData.get(p.getUniqueId()).getKills());
			p.sendMessage(" ");
			p.sendMessage(Messages.BORDER.getValue());
		}
	}
	
	public void broadcastEndgame()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.sendMessage(Messages.BORDER.getValue());
			p.sendMessage(" ");
			p.sendMessage((places[0] == null) ? ChatColor.YELLOW + "        First place: " + ChatColor.GRAY + "Nobody" : ChatColor.YELLOW + "        First place:" + ChatColor.GREEN + places[0] + ChatColor.YELLOW + ", " + ChatColor.GREEN + playerData.get(Bukkit.getPlayer(places[0])).getKills() + ChatColor.YELLOW + " kills.");
			p.sendMessage((places[1] == null) ? ChatColor.YELLOW + "       Second place: " + ChatColor.GRAY + "Nobody" : ChatColor.YELLOW + "       Second place:" + ChatColor.GREEN + places[1] + ChatColor.YELLOW + ", " + ChatColor.GREEN + playerData.get(Bukkit.getPlayer(places[1])).getKills() + ChatColor.YELLOW + " kills.");
			p.sendMessage((places[2] == null) ? ChatColor.YELLOW + "        Third place: " + ChatColor.GRAY + "Nobody" : ChatColor.YELLOW + "        Third place:" + ChatColor.GREEN + places[2] + ChatColor.YELLOW + ", " + ChatColor.GREEN + playerData.get(Bukkit.getPlayer(places[2])).getKills() + ChatColor.YELLOW + " kills.");
			p.sendMessage(" ");
			p.sendMessage(Messages.BORDER.getValue());
		}
	}
	
	public void startQuake()
	{
		GameActive = false;
		endTrigger = false;
		setWorld("spawn", 0);
		if(Bukkit.getOnlinePlayers().size() >= FILLED_PLAYERS)
			startCountdown(FILLED_START);
		else
			startCountdown(DEFAULT_START);
		GameEnabled = true;
		/*new BukkitRunnable()
		{
			
			public void run()
			{
				if(GameEnabled && !GameActive)
				{
					for(Player p : Bukkit.getOnlinePlayers())
					{
						p.sendMessage(Messages.TIPS.getValue() + TIPS[(int) (Math.random() * TIPS.length)]);	
					}
				} else
					this.cancel();
			}
			
		}.runTaskLater(plugin, (int) (Math.random() * 8 + 8));
		*/
		currentData = (int) (Math.random() * (worldData.size() - 1) + 1);
	}
	
	static
	{
		ammoMaterials.put(WeaponType.GAUNTLET, null);
		ammoMaterials.put(WeaponType.MACHINEGUN, Material.WOOD_HOE);
		ammoMaterials.put(WeaponType.SHOTGUN, Material.WOOD_AXE);
		ammoMaterials.put(WeaponType.GRENADE_LAUNCHER, Material.WOOD_SPADE);
		ammoMaterials.put(WeaponType.ROCKET_LAUNCHER, Material.WOOD_PICKAXE);
		ammoMaterials.put(WeaponType.PLASMAGUN, Material.WOOD_SWORD);
		ammoMaterials.put(WeaponType.RAILGUN, Material.STONE_AXE);
		ammoMaterials.put(WeaponType.LIGHTNING_GUN, Material.STONE_HOE);
		ammoMaterials.put(WeaponType.BFG9K, null);
		
		weaponIndices.put(WeaponType.GAUNTLET, new Integer(0));
		weaponIndices.put(WeaponType.MACHINEGUN, new Integer(1));
		weaponIndices.put(WeaponType.SHOTGUN, new Integer(2));
		weaponIndices.put(WeaponType.GRENADE_LAUNCHER, new Integer(3));
		weaponIndices.put(WeaponType.ROCKET_LAUNCHER, new Integer(4));
		weaponIndices.put(WeaponType.PLASMAGUN, new Integer(5));
		weaponIndices.put(WeaponType.RAILGUN, new Integer(6));
		weaponIndices.put(WeaponType.LIGHTNING_GUN, new Integer(7));
		weaponIndices.put(WeaponType.BFG9K, new Integer(8));
		
		ammoValues.put(WeaponType.MACHINEGUN, new AmmoPickup(WeaponType.MACHINEGUN, 50, 20));
		ammoValues.put(WeaponType.SHOTGUN, new AmmoPickup(WeaponType.SHOTGUN, 10, 20));
		ammoValues.put(WeaponType.GRENADE_LAUNCHER, new AmmoPickup(WeaponType.GRENADE_LAUNCHER, 10, 30));
		ammoValues.put(WeaponType.ROCKET_LAUNCHER, new AmmoPickup(WeaponType.ROCKET_LAUNCHER, 10, 30));
		ammoValues.put(WeaponType.PLASMAGUN, new AmmoPickup(WeaponType.PLASMAGUN, 25, 30));
		ammoValues.put(WeaponType.RAILGUN, new AmmoPickup(WeaponType.RAILGUN, 10, 30));
		ammoValues.put(WeaponType.LIGHTNING_GUN, new AmmoPickup(WeaponType.LIGHTNING_GUN, 50, 30));
		ammoValues.put(WeaponType.BFG9K, new AmmoPickup(WeaponType.BFG9K, 10, 60));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args)
	{
		if(sender instanceof Player && sender.isOp())
		{
			if(cmd.getName().equalsIgnoreCase("givewep") && args.length == 1 && GameActive && playerData.keySet().contains(((Player) sender).getUniqueId()))
			{
				try
				{
					Player player = (Player) sender;
					int i = Integer.valueOf(args[0]);
					if(i > 8 || i < 0)
						i = 1;
					WeaponType[] tempArr = {WeaponType.GAUNTLET, WeaponType.MACHINEGUN, WeaponType.SHOTGUN, WeaponType.GRENADE_LAUNCHER, WeaponType.ROCKET_LAUNCHER, WeaponType.PLASMAGUN, WeaponType.RAILGUN, WeaponType.LIGHTNING_GUN, WeaponType.BFG9K};
					playerData.get(player.getUniqueId()).setWeapon(WepData.getWeaponInstance(tempArr[i]), i);
					playerData.get(player.getUniqueId()).setAmmo(200, i);
					player.getInventory().setItem(i, WepData.getWeaponInstance(tempArr[i]).getItem());
				} catch(Exception e)
				{
					e.printStackTrace();
					Bukkit.getLogger().warning("You done messed up.");
					return false;
				}
				return true;
			} else if(cmd.getName().equalsIgnoreCase("game-enable"))
				GameEnabled = true;
			else if(cmd.getName().equalsIgnoreCase("game-disable"))
				GameEnabled = false;
			else if(cmd.getName().equalsIgnoreCase("particle-test"))
			{
				Player p = (Player) sender;
				Packets.sendPacket(p, Particles.createParticle(true, thekian.nms.protocol.Particles.ParticleTypeEnum.CLOUD, (float) p.getLocation().getX(), (float) p.getLocation().getY(), (float) p.getLocation().getZ(), 1, 1, 1, 0, (int) Math.ceil(50)));
			}
			else if(cmd.getName().equalsIgnoreCase("quake-start"))
			{
				if(GameActive)
					resetPluginInstance();
				startQuake();
			} else if(cmd.getName().equalsIgnoreCase("quake-end"))
			{
				if(GameActive)
					if(getAlivePlayers() <= MIN_PLAYERS_END && !endTrigger)
					{
						endTrigger = true;
						broadcastEndgame();
						if(getAlivePlayers() == 1)
							for(PlayerData pd : playerData.values())
								if(pd.getAlive())
								{
									places[0] = pd.getPlayer().getName();
									setSpectator(pd.getPlayer(), true);
								}
						gameEndRunnable.runTaskLater(plugin, 100);
					}
			}
		}
		return false;
	}
}
