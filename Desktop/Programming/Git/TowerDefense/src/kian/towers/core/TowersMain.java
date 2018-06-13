package kian.towers.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Arrow.PickupStatus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import kian.towers.tower.Schematic;
import kian.towers.tower.Tower;
import kian.towers.tower.Tower.TargetEnum;
import kian.towers.tower.implementation.*; //Imports all tower types
import kian.towers.util.ZipUtils;
import kian.towers.core.WorldData.WorldType;
import kian.towers.enemy.MobData;
import kian.towers.enemy.Wave;
import net.md_5.bungee.api.ChatColor;
import thekian.nms.protocol.Packets;
import thekian.nms.protocol.Titles;

/*
Current classes:
	
attacks
	AOEField.java - Complete
	CustomProjectile.java - Complete
	ParticleBeam.java - Complete
	ParticleProjectile.java - Complete
	ParticleSpell.java - Complete
	ParticleTracing.java - Complete
	ParticleTracingSpell.java - Complete
core
	BasicEvents.java - Complete
	CoordSet.java - Complete
	GameState.java - Complete
	PlayerData.java - Complete
	Processor.java - Complete
	TowersMain.java
	WorldData.java - Complete
enemy
	MobData.java - Complete
	Wave.java - Complete
tower/implementation
	ArrowTower.java - Complete
	DragonTower.java - Complete
	PriestTower.java - Complete
	TeslaTower.java - Complete
tower
	AttackData.java - Complete
	AttackType.java - Complete
	BlockData.java - Complete
	Schematic.java - Complete
	SplashData.java - Complete
	Tower.java - Complete
util
	ZipUtils.java - Complete

*/

/***
 * Minecraft tower defense. Makes extensive use of polymorphism for tower design. Mobs are AI-stripped and are moved toward node coordinates.
 * Towers and main class are dependent on Processor.class, which is regulated by but not dependent on aforementioned classes.
 * The plugin's code structure is designed to avoid pathological coupling, which is why most events are handled in the main class.
 * 
 * The code is heavily commented for readability as an example of work, and also in the highly unlikely event that its source is released for people to work on.
 * As this is intended as an example of minigame design and not as a standalone plugin, it lacks external configuration and hooks for
 * integration into a minigame engine.
 * @author TheKian
 *
 */
public class TowersMain extends JavaPlugin implements Listener
{
	private Plugin plugin;
	private String serverPath, pluginPath;
	private List<UUID> spectators = new ArrayList<UUID>(); //Spectators get hidden during the game
	private List<WorldData> worldData = new ArrayList<WorldData>(); //Contains world data for each world
	private HashMap<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>(); //Holds metadata for each player
	private Wave[] waves = new Wave[20]; //Contains information on each wave
	private int currentWave = 0; //The current wave for the map. Global variable for use in the wave spawning runnable.
	private int tmp1 = 0, tmp2 = 0, tmp3 = 0; boolean tmp5 = false, tmp6 = false; //Global variables for runnables to use
	
	final int DEFAULT_START = 30;
	final int FILLED_START = 10;
	final int[] ALERT_TIMES = {30, 20, 10, 5, 4, 3, 2, 1};
	final int FILLED_PLAYERS = 4;
	final int MIN_PLAYERS = 2;
	final int MIN_PLAYERS_END = 0;
	
	private CoordSet corner1, corner2; int progress = 0; Location center;//Unsophisticated way to export towers
	
	private GameState state; //State in which the game is at present
	private boolean lastSpawned; //Whenever the last enemy has been spawned, this becomes true. After all enemies are gone, the game ends.
	private World currentWorld; //World which is currently loaded
	private int currentData; //Index of loaded world
	
	//Basic tower instances. Used for their descriptions. Also load schematics for future convenience. Footprint should be negligible and is preferable to needless extra code.
	private ArrowTower arrowTowerGlobal = new ArrowTower(null, null);
	private PriestTower priestTowerGlobal = new PriestTower(null, null);
	private TeslaTower teslaTowerGlobal = new TeslaTower(null, null);
	private DragonTower dragonTowerGlobal = new DragonTower(null, null);
	private List<Tower> globalTowers = new ArrayList<Tower>();
	
	@Override
	public void onEnable()
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, this); //In order to avoid potential pathological coupling, most events are handled by the main class.
		Bukkit.getServer().getPluginManager().registerEvents(new BasicEvents(this), this); //Basic, non-game-dependent events, like hunger handling.
		
		plugin = this;
		state = GameState.DISABLED;
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
		///////////////////////////////////////////////////////
		//###################################################//
		//# If the various folders which contain game		#//
		//# data do not exist, then they are created.		#//
		//# Absent data is still going to cause errors		#//
		//# later on when attempting to load default data.	#//
		//# Custom data must be added to the folders		#//
		//# manually, except for generated schematics.		#//
		//###################################################//
		///////////////////////////////////////////////////////
		if(!(new File(pluginPath + "TowerWorldData")).exists()) //Contains WorldData sources
		{
			new File(pluginPath + "TowerWorldData").mkdirs();
		}
		if(!(new File(pluginPath + "TowerWaveData")).exists()) //Contains wave data
		{
			new File(pluginPath + "TowerWaveData").mkdirs();
		}
		if(!(new File(pluginPath + "TowerData")).exists()) //Contains tower schematics
		{
			new File(pluginPath + "TowerData").mkdirs();
		}
		if(!(new File(serverPath + "spawn")).exists()) //Makes sure that there is a spawn world folder available
		{
			new File(serverPath + "spawn").mkdirs();
		}
		//Log the server and plugin path to the console so we know that the server is looking in the right location
		Bukkit.getLogger().info("Server path: " + serverPath);
		Bukkit.getLogger().info("Plugin path: " + pluginPath);
		//Loads 20 wave files into the waves array

		Bukkit.getLogger().info("Attempting to load wave information files from " + pluginPath + "TowerWaveData" + File.separator);
		for(int i = 1; i <= 20; i++)
		{
			waves[i - 1] = new Wave(new File(pluginPath + "TowerWaveData" + File.separator + i + ".dat"));
		}
		Bukkit.getLogger().info("Attempting to load " + pluginPath + "TowerWorldData" + File.separator + "spawn.dat");
		//Default spawn loader. If there is no spawn data, the minigame will likely not work correctly.
		worldData.add(new WorldData(new File(pluginPath + "TowerWorldData" + File.separator + "spawn.dat"), true));
		//Looks through the world data folder and loads WorldData for all non-spawn worlds
		for(File f : (new File(pluginPath + "TowerWorldData")).listFiles())
		{
			if(!f.getName().equalsIgnoreCase("spawn.dat"))
			{
				Bukkit.getLogger().info("Attempting to load " + f.getPath());
				worldData.add(new WorldData(f, false));
			}
		}
		
		//Tower instances are defined earlier to prevent potential compiler errors.
		makeArrowGlobal();
		makePriestGlobal();
		makeTeslaGlobal();
		makeDragonGlobal();
		//To make code more efficient in tower placement, global towers are stored in a list
		globalTowers.add(arrowTowerGlobal);
		globalTowers.add(priestTowerGlobal);
		globalTowers.add(teslaTowerGlobal);
		globalTowers.add(dragonTowerGlobal);
		
		//Actually load the worlds
		for(int i = 0; i < worldData.size(); i++)
			loadWorld(i);
		
		//###################################################
		//#			          SCHEDULERS             		#
		//# Doesn't need clunkier Bukkit schedular because  #
		//# of the far simpler BukkitRunnable class. I don't#
		//# know which is considered 'proper', but it is a  #
		//# preferable API. Various repeated tasks.         #
		//###################################################
		
		//Controls mob movement when the game is active 
		new BukkitRunnable(){
			
			public void run()
			{
				if(state.equals(GameState.GAME_ACTIVE))
					for(PlayerData pd : playerData.values())
						for(LivingEntity le : pd.getPlayerMobs())
						{
							if(Processor.getEnemies().containsKey(le))
							{
								MobData md = Processor.getEnemies().get(le);
								Location loc = le.getLocation();
								CoordSet cs = pd.getNodes().get(md.getNode());
								Vector v = new Vector(cs.getX() - loc.getX(), cs.getY() - loc.getY(), cs.getZ() - loc.getZ());
								if(v.length() != 0)
									v = v.normalize().multiply(md.getWalkspeed());
								if(le.isInsideVehicle())
									le.leaveVehicle();
								//Set direction of the mob
								loc.setDirection(v.length() != 0 ? v.clone().normalize() : v);
								try
								{
									le.setVelocity(v);
									le.teleport(loc);
									
								} catch(IllegalArgumentException e)
								{
									System.out.println("Illegal x velocity thing");
									System.out.println("x: " + (cs.getX() - loc.getX()) + ", y: " + (cs.getY() - loc.getY()) + ", z: " + (cs.getZ() - loc.getZ()));
									System.out.println("Mob speed: " + md.getWalkspeed() + ", Velocity: " + v.length());
								}
							}
						}
			}
			
		}.runTaskTimer(plugin, 10, 5);
		
		//Checks if a player has lost all his lives and checks game ending
		new BukkitRunnable(){
			
			public void run()
			{
				if(state.equals(GameState.GAME_ACTIVE))
				{
					for(Iterator<UUID> iterator = playerData.keySet().iterator(); iterator.hasNext(); )
					{
						UUID uid = iterator.next();
						PlayerData pd = playerData.get(uid);
						if(pd.getLives() <= 0)
						{
							Player p = Bukkit.getPlayer(uid);
							p.sendMessage(ChatColor.DARK_RED + "Your base has fallen.");
							p.sendMessage(ChatColor.YELLOW + "Your total net assets were: " + ChatColor.GREEN + pd.calculateNetAssets() + ChatColor.YELLOW + " essence");
							//In an actual implementation of this game, net assets would give you some amount of game currency
							for(Tower t : pd.getTowers())
								t.destroyTower();
							iterator.remove(); //Get rid of dead player data
							spectators.add(uid);
							hideUnhideSpectators(true);
						}
					}
					//If everyone died
					if(playerData.values().size() == 0)
					{
						state = GameState.GAME_ENDED;
						for(Player p : Bukkit.getOnlinePlayers())
							p.sendMessage(ChatColor.RED + "All players have perished to the monstrous tide!");
						new BukkitRunnable(){
							
							public void run()
							{
								startTDGame();
							}
							
						}.runTaskLater(plugin, 100);
					} else if(lastSpawned && Processor.getTargets().size() == 0) //Otherwise if they've won
					{
						state = GameState.GAME_ENDED;
						new BukkitRunnable(){
							
							public void run()
							{
								startTDGame();
							}
							
						}.runTaskLater(plugin, 100);
					}
						
				}
			}
			
		}.runTaskTimer(plugin, 10, 5);
		
		//Separate runnable to execute more often without reducing performance as much
		//Tells the mob to go to the next node
		new BukkitRunnable(){
			
			public void run()
			{
				if(state.equals(GameState.GAME_ACTIVE))
					for(PlayerData pd : playerData.values())
						for(Iterator<LivingEntity> iterator = pd.getPlayerMobs().iterator(); iterator.hasNext();)
						{
							LivingEntity le = iterator.next();
							MobData md = Processor.getEnemies().get(le);
							if(Processor.getEnemies().containsKey(le))
							{
								//If the mob hasn't reached the last node
								if(md.getNode() < pd.getNodes().size() - 1)
								{
									if(a(le.getLocation(), pd.getNodes().get(md.getNode())) < 1)
										md.incrementNode();
								} else //Otherwise, hurt the player and get rid of the mob
								{
									if(a(le.getLocation(), pd.getNodes().get(md.getNode())) < 1)
									{
										pd.damage(md.getDamage());
										iterator.remove();
										Processor.getEnemies().remove(le);
										Processor.removeTarget(le);
										le.remove();
									}
								}
							}
						}
			}
			
		}.runTaskTimer(plugin, 10, 2);
		
		//Run processor
		new BukkitRunnable(){
			
			public void run()
			{
				if(state.equals(GameState.GAME_ACTIVE))
				{
					Processor.processTowerAttacks(playerData.values(), plugin);
					Processor.process(plugin);
				}
			}
			
		}.runTaskTimer(plugin, 10, 1);
		
		//HUD, tells player number of lives and amount of money
		new BukkitRunnable(){
			public void run()
			{
				if(state.equals(GameState.GAME_ACTIVE))
				{
					for(Player p : Bukkit.getOnlinePlayers())
					{
						if(playerData.keySet().contains(p.getUniqueId()))
						{
							PlayerData pd = playerData.get(p.getUniqueId());
							String s = (ChatColor.RED + "Lives: " + ChatColor.YELLOW + pd.getLives() + "   " + ChatColor.RED + "Essence: " + ChatColor.YELLOW + pd.getMoney());
							Packets.sendPacket(p, Titles.createHotbarPacket(s));
						}
					}
				}
			}
		}.runTaskTimer(plugin, 10, 4);
		
		//Keep the time at noon when the plugin is running
		new BukkitRunnable(){

			public void run()
			{
				if(!state.equals(GameState.DISABLED) && currentWorld != null)
					currentWorld.setTime(6000);
			}
			
		}.runTaskTimer(plugin, 10, 10);
		
		//Periodically clears dead arrows
		new BukkitRunnable(){
			
			public void run()
			{
				if(state.equals(GameState.GAME_ACTIVE))
					for(Entity e : currentWorld.getEntities())
						if(e instanceof Arrow && e.getVelocity().length() == 0)
							e.remove();
			}
			
		}.runTaskTimer(plugin, 10, 100);
		
		//Game start code
		new BukkitRunnable(){
			
			public void run()
			{
				if(state.equals(GameState.SPAWN))
					if(Bukkit.getOnlinePlayers().size() >= MIN_PLAYERS)
						if(Bukkit.getOnlinePlayers().size() >= FILLED_PLAYERS)
							startCountdown(FILLED_START);
						else
							startCountdown(DEFAULT_START);
			}
			
		}.runTaskTimer(plugin, 0, 20);
	}
	
	//Schedules the next wave to happen
	private void scheduleWave(int i)
	{
		new BukkitRunnable(){

			public void run()
			{
				Wave cw = waves[currentWave];
				if(tmp3 == 0 && tmp2 == 0 && currentWave > 0)
				{
					for(UUID uid : playerData.keySet())
					{
						Bukkit.getPlayer(uid).sendMessage(ChatColor.GREEN + "You received " + ChatColor.YELLOW + (currentWave * 500) + ChatColor.GREEN + " essence at the beginning of the round.");
						playerData.get(uid).addMoney(currentWave * 500);
					}
				}
				tmp1 = cw.getNumbers()[tmp2]; //Cycles through the different sets of mobs in each wave
				if(tmp3 < tmp1)
				{
					for(PlayerData pd : playerData.values()) //Spawns a mob for each player present
					{
						CoordSet cs = pd.getMobSpawn(); //Gets player's spawn gate location
						MobData md = cw.getMobData()[tmp2]; //Mob special data
						Location loc = new Location(currentWorld, cs.getX(), md.getLevel() >= 3 ? cs.getY() + 3 : cs.getY(), cs.getZ());
						if(!loc.getChunk().isLoaded()) //Loads spawn chunk if nobody is there
							loc.getChunk().load();
						LivingEntity enemy = (LivingEntity) currentWorld.spawnEntity(loc, cw.getMobTypes()[tmp2]); //Spawns a mob at the gate
						//enemy.setAI(false); //Removes normal AI //But prevents normal velocity, requiring teleporting instead
						enemy.setRemoveWhenFarAway(false); //Prevents despawning
						enemy.setCollidable(false); //Prevents being bumped
						if(enemy instanceof Zombie)
							((Zombie) enemy).setBaby(false);
						cw.getMobData()[tmp2].initialize(enemy); //Initializes mob data information
						pd.getPlayerMobs().add(enemy); //Adds mob reference to the player mob list
						Processor.getEnemies().put(enemy, cw.getMobData()[tmp2].clone());
						Processor.getTargets().add(enemy);
					}
					tmp3++; //Progress in spawning each part of the wave
					scheduleWave(10); //Spawns next mob one half of a second later
					return;
				} else if(tmp2 < waves[currentWave].getSize()) //All parts of the wave are the same length
				{
					tmp2++; //Moves on to spawning the next enemies
					tmp3 = 0;
				}

				if(tmp2 >= waves[currentWave].getSize())
				{
					tmp5 = true; //Wave has finished spawning
					//Resets values
					tmp2 = 0;
					tmp3 = 0;
					currentWave++; //Increments wave
					if(currentWave < waves.length)
						scheduleWave(400); //Spawns next wave 20 seconds later
					else
						lastSpawned = true; //Final mobs have been spawned
					return;
				}

				scheduleWave(80 - 3 * currentWave); //Spawns next section of wave 4-1 seconds later, depending on the wave
				return;
			}

		}.runTaskLater(plugin, Math.max(1, i));
	}
	
	public void startCountdown(int i)
	{
		state = GameState.STARTING;
		if(Bukkit.getOnlinePlayers().size() == 0)
			return;
		if(i <= 0)
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				p.closeInventory();
				p.sendMessage(ChatColor.GRAY + "Game beginning now.");
				p.sendMessage(ChatColor.RED + "The first wave begins in 60 seconds.");
			}
			System.out.println("Setting game world.");
			int index = (int) (Math.random() * (worldData.size() - 1)) + 1;
			setWorld(worldData.get(index).getWorldName(), index);
			state = GameState.GAME_ACTIVE;
			hideUnhideSpectators(true);
			scheduleWave(1200); //Start spawning waves in 60 seconds
		}
		for(int i2 : ALERT_TIMES)
			if(i2 == i)
				for(Player p : Bukkit.getOnlinePlayers())
					p.sendMessage(ChatColor.GREEN + "" + i + ChatColor.GRAY + " seconds left.");
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
	
	//Loads a world onto the server from a .zip file.
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
	
	public void setWorld(String name, int index)
	{
		currentWorld = Bukkit.getServer().getWorld(name);
		currentData = index;
		WorldData currentWorldData = worldData.get(index);
		System.out.println(currentWorldData.getWorldType());
		if(currentWorldData.getWorldType().equals(WorldType.SPAWN))
		{
			CoordSet cs = currentWorldData.getStartLocations().get(0);
			for(Player p : Bukkit.getOnlinePlayers())
			{
				p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
				p.setInvulnerable(true);
				p.setCanPickupItems(false);
				p.setAllowFlight(false);
				p.resetMaxHealth();
				p.getInventory().clear();
				p.setGameMode(GameMode.SURVIVAL);
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
			playerData.clear();
			int currentPlayers = 0;
			for(Player p : Bukkit.getOnlinePlayers())
			{
				PlayerData pd = null;
				if(currentPlayers < 4)
				{
					pd = new PlayerData(currentWorldData.getStartLocations().get(currentPlayers), currentWorldData.getStartLocations().get(currentPlayers), a(currentPlayers));
					pd.setNodes(currentWorldData.getNodes(currentPlayers));
					playerData.put(p.getUniqueId(), pd);
					
					//Tower editing tool
						ItemStack is = new ItemStack(Material.IRON_SPADE);
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(ChatColor.YELLOW + "Building Tool");
						is.setItemMeta(im);
						p.getInventory().setItem(0, is);
				}
				CoordSet cs = pd == null ? currentWorldData.getSpectatorStartLocation() : pd.getStartLoc();
				currentPlayers++;
				p.setInvulnerable(true);
				p.setCanPickupItems(false);
				p.setAllowFlight(true);
				p.setGameMode(GameMode.SURVIVAL);
				p.resetMaxHealth();
				p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
				new BukkitRunnable(){ //Extra delayed teleport to make sure player ends up in the correct world location 

					public void run()
					{
						p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
					}

				}.runTaskLater(plugin, 2);
			}
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e)
	{
		if(state.equals(GameState.GAME_ACTIVE))
		{	
			e.setCancelled(true);
			e.setTarget(null);
		}
		
	}
	
	//Teleports player out of void
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{
		if(e.getCause().equals(DamageCause.VOID) && e.getEntity() instanceof Player && !state.equals(GameState.DISABLED))
		{
			CoordSet cs = worldData.get(currentData).getStartLocations().get(0);
			e.getEntity().teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()));
			e.setCancelled(true);
		}
	}

	//Requires access to the main class' global variables.
	//Handles tower creation/placement and custom GUI acces.
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		//#####################################################################################
		//#																					  #
		//# Non-game related code section - used for exporting tower schematics from in-game. #
		//#																					  #
		//#####################################################################################
		if(progress > 0 && e.getPlayer().getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot()).getType().equals(Material.STICK) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand().equals(EquipmentSlot.HAND))
		{
			
			Block b = e.getClickedBlock();
			if(progress == 1)
			{
				progress = 2;
				corner1 = new CoordSet(b.getX(), b.getY(), b.getZ());
				e.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "Selected corner 1 at " + b.getX() + " " + b.getY() + " " + b.getZ() + ".");
				return;
			} else if(progress == 2)
			{
				progress = 3;
				corner2 = new CoordSet(b.getX(), b.getY(), b.getZ());
				e.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "Selected corner 2 at " + b.getX() + " " + b.getY() + " " + b.getZ() + ".");
				return;
			}
			else if(progress == 3)
			{
				progress = 0;
				center = b.getLocation();
				e.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "Selected center at " + b.getX() + " " + b.getY() + " " + b.getZ() + ".");
				return;
			}
		}
		//##########################################################
		
		if(!state.equals(GameState.DISABLED))
			e.setCancelled(true);
		
		if(playerData.keySet().contains(e.getPlayer().getUniqueId()))
		{
			Player p = e.getPlayer();
			PlayerData pd = playerData.get(p.getUniqueId());
			ItemStack heldItem = p.getInventory().getItem(p.getInventory().getHeldItemSlot()); //To help with code readability 
			Block b = e.getClickedBlock();
			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand().equals(EquipmentSlot.HAND) && heldItem.getType().equals(Material.IRON_SPADE)) //If the player right clicks on a block and is holding the tower build tool
			{
				//If the block is stained clay of the appropriate color for the player, do tower placement
				if(b.getType().equals(Material.STAINED_CLAY) && b.getData() == (byte) pd.getData())
				{
					createTowerGUI(p, pd, e.getClickedBlock(), (byte) pd.getData());
				} else //Always at the end because it needs to cycle through every tower
					//If the player clicks on a tower, open editor GUI
				{
					Tower t = blockInsideTower(pd, e.getClickedBlock());
					if(t != null)
						editTowerGUI(p, pd, t);
				}
			}
		}
	}
	
	//Somewhat questionable way to allow arrows to pass through blocks
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
		if(e.getEntity() instanceof Arrow)
			((Arrow) e.getEntity()).setPickupStatus(PickupStatus.DISALLOWED);
		if(e.getHitEntity() == null)
		{
			final Vector v = e.getEntity().getVelocity().clone();
			new BukkitRunnable(){

				public void run()
				{
					e.getEntity().teleport(e.getEntity().getLocation().add(v.clone().normalize().multiply(3)));
					e.getEntity().setVelocity(v);
					Processor.remakeProjectile(e.getEntity(), v);
				}
			}.runTaskLater(plugin, 1);
		} else
		{
			if(e.getHitEntity() instanceof LivingEntity)
			{
				Processor.processProjectile(e.getEntity(), (LivingEntity) e.getHitEntity(), plugin, true);
			}
			e.getEntity().remove();
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
		if(state.equals(GameState.GAME_ACTIVE))
		{
			e.setDroppedExp(0);
			for(PlayerData pd : playerData.values())
				if(pd.getPlayerMobs().remove(e.getEntity()))
				{
					pd.addMoney(Processor.getEnemies().get(e.getEntity()).getEssence());
				}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		//If types are valid, process projectile. If it's false (e.g. not a custom projectile), don't cancel.
		if(e.getDamager() instanceof Projectile && e.getEntity() instanceof LivingEntity)
		{
			System.out.println("Projectile has hit an entity.");
			boolean b = Processor.processProjectile((Projectile) e.getDamager(), (LivingEntity) e.getEntity(), plugin, true);
			if(b)
				e.setDamage(0);
			e.getDamager().remove();
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if(state.equals(GameState.GAME_ACTIVE))
		{
			CoordSet cs = worldData.get(currentData).getSpectatorStartLocation();
			e.getPlayer().teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()));
			spectators.add(e.getPlayer().getUniqueId());
			for(UUID uid : spectators)
				if(Bukkit.getPlayer(uid).isOnline())
					e.getPlayer().hidePlayer(Bukkit.getPlayer(uid));
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		spectators.remove(e.getPlayer().getUniqueId());
		if(playerData.keySet().contains(e.getPlayer().getUniqueId()))
		{
			PlayerData pd = playerData.get(e.getPlayer().getUniqueId());
			for(Tower t : pd.getTowers())
				t.destroyTower();
			for(LivingEntity le : pd.getPlayerMobs())
			{
				le.remove();
				Processor.getTargets().remove(le);
			}
			playerData.remove(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{
		if(state.equals(GameState.GAME_ACTIVE))
		{
			e.setCancelled(true);
			String s;
			PlayerData pd = playerData.get(e.getPlayer().getUniqueId());
			if(pd != null)
			{
				s = b(pd.getData()) + e.getPlayer().getName() + ChatColor.WHITE + ": " + e.getMessage();
			}
			else
				s = ChatColor.GRAY + e.getPlayer().getName() + ChatColor.WHITE + ": " + e.getMessage();
			
			for(Player p : Bukkit.getOnlinePlayers())				
				p.sendMessage(s);
		} else if(!state.equals(GameState.DISABLED))
		{
			e.setCancelled(true);
			for(Player p : Bukkit.getOnlinePlayers())
				p.sendMessage(ChatColor.YELLOW + e.getPlayer().getName() + ChatColor.WHITE + ": " + e.getMessage());
				
		}
	}
	
	//Primarily handles custom GUI stuff.
	//Most important is probably the tower building inventory.
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if(!(e.getWhoClicked() instanceof Player && playerData.containsKey(((Player) e.getWhoClicked()).getUniqueId())))
			return; //Return if not a valid player with a playerData entry.
		Player p = (Player) e.getWhoClicked();
		PlayerData pd = playerData.get(p.getUniqueId());
		//Building tower GUI
		if(e.getInventory().getName().equals("Build Tower"))
		{
			e.setCancelled(true); //Stop regular inventory function
			ItemStack is = e.getCurrentItem(); //Faster to call method only once 
			ItemMeta locdat = e.getInventory().getItem(4).getItemMeta(); //Middle item on the first row; block with location data
			int x = Integer.valueOf(locdat.getLore().get(0).substring(2)); //First character is the color code.
			int y = Integer.valueOf(locdat.getLore().get(1).substring(2)); //Lore has three lines with block coordinates.
			int z = Integer.valueOf(locdat.getLore().get(2).substring(2)); //Sort of hacky, but avoids bloating player data or global variables.
			Location location = new Location(p.getWorld(), x, y + 1, z);
			for(Tower t : globalTowers)
			{
				if(is == null || is.getType().equals(Material.AIR) || is.getItemMeta() == null)
					return;
				//If the tower can be built...
				if(is.getItemMeta().getDisplayName().equals(ChatColor.GRAY + t.getName(0)))
				{
					//Test to make sure that there is enough ground space for the tower
					int[] dims = t.getStructure().getMinMax();
					System.out.println("-x: " + dims[0] + ", +x = " + dims[1] + ", -y = " + dims[2] + ", +y = " + dims[3] + ", -z = " + dims[4] + ", +z = " + dims[5]);
					for(int xval = dims[0]; xval <= dims[1]; xval++)
						for(int zval = dims[4]; zval <= dims[5]; zval++)
							if(!(p.getWorld().getBlockAt(x + xval, y, z + zval).getType().equals(Material.STAINED_CLAY) && p.getWorld().getBlockAt(x + xval, y, z + zval).getData() == pd.getData()))
							{
								p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
								return;
							}
					if(pd.getMoney() >= Math.round(250 * t.getMultiplier()) && !towerIntersects(t, location))
					{
						Tower newTower;
						try {
							newTower = t.getClass().getConstructor(Schematic.class, Player.class).newInstance(t.getStructure(), p);
							pd.addMoney((int) Math.round(-250 * t.getMultiplier()));
							pd.addTower(newTower);
							newTower.buildTower(location);
							p.closeInventory();
						} catch (Exception ex)
						{ ex.printStackTrace(); }
						//new ArrowTower(t.getStructure(), p);	
					} else
					{
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
						p.closeInventory();
					}
				}
			}
		} else if(e.getInventory().getName().equals("Tower Settings"))
		{
			Inventory inv = e.getInventory();
			if(pd.getSelection() < 0 || pd.getSelection() >= pd.getTowers().size()) //If selection is invalid for some reason, stop everything
			{
				p.closeInventory();
				return;
			}
			Tower t = pd.getTowers().get(pd.getSelection());
			ItemStack is = e.getCurrentItem();
			if(is == null)
				return;
			if(is.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Attack Closest"))
			{
				t.setTargetType(TargetEnum.CLOSEST);
				inv.getItem(9).setDurability((short) 10); //Set to green
				inv.getItem(18).setDurability((short) 8); //Set others to gray
				inv.getItem(27).setDurability((short) 8);
			} else if(is.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Attack Strongest"))
			{
				t.setTargetType(TargetEnum.STRONGEST);
				inv.getItem(9).setDurability((short) 8); //Set others to to gray
				inv.getItem(18).setDurability((short) 10); //Set to green
				inv.getItem(27).setDurability((short) 8);
			} else if(is.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Attack First"))
			{
				t.setTargetType(TargetEnum.FIRST);
				inv.getItem(9).setDurability((short) 8); //Set others to to gray
				inv.getItem(18).setDurability((short) 8);
				inv.getItem(27).setDurability((short) 10); //Set to green
			} else if(is.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Sell this tower")) //Sells and destroys tower, returns 75% of cost
			{
				pd.addMoney((int) (0.75 * t.getValue()));
				p.closeInventory();
				t.destroyTower();
				pd.getTowers().remove(t);
				pd.setSelection(-1);
			} else if(is.getType().equals(Material.GOLD_BLOCK)) //First upgrade
			{
				if(is.equals(inv.getItem(12)))
				{
					int cost = (int) (25 * Math.pow(2, t.getUpgradeLevel(1) + 1) * t.getMultiplier());
					if(pd.getMoney() >= cost)
					{
						pd.addMoney(-cost);
						t.upgradeTower(1);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 1);
					} else
					{
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
					}
				} else if(is.equals(inv.getItem(14))) //Second upgrade
				{
					int cost = (int) (25 * Math.pow(2, t.getUpgradeLevel(2) + 1) * t.getMultiplier());
					if(pd.getMoney() >= cost)
					{
						pd.addMoney(-cost);
						t.upgradeTower(2);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 1);
					} else
					{
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
					}
				}
				p.closeInventory();
			}
		}
	}

	/***
	 * Creates a custom GUI for editing a tower.
	 * @param p
	 * @param pd
	 * @param t
	 */
	private void editTowerGUI(Player p, PlayerData pd, Tower t)
	{
		pd.setSelection(t);

		//Inventory
		Inventory inv = Bukkit.createInventory(p, 36, "Tower Settings");
		
		//Definitions
		ItemStack is = new ItemStack(Material.EMERALD); //Item containing data on the tower
		ItemMeta im = is.getItemMeta();
		
		//Tower information
		im.setDisplayName(ChatColor.YELLOW + t.getName(0));
		im.setLore(Arrays.asList(new String[]{
				ChatColor.YELLOW + "Upgrade level: " + ChatColor.GREEN + t.getUpgradeLevel(1) + ChatColor.GRAY + "/" + ChatColor.GREEN + t.getUpgradeLevel(2),
				ChatColor.YELLOW + "Damage: " + ChatColor.GREEN + ((t.getAttackData().getDamage() == 0 && t.getAttackData().getSplashData() != null) ? t.getAttackData().getSplashData().getDamage() : t.getAttackData().getDamage()),
				ChatColor.YELLOW + "Speed: " + ChatColor.GREEN + t.getAttackData().getSpeed() + ChatColor.GRAY + " ticks per attack",
				ChatColor.YELLOW + "Attacks: " + ChatColor.GREEN + t.getAttackData().getAttacks()
		}));
		is.setItemMeta(im);
		
		inv.setItem(4, is);
		
		//Attack types
		is = new ItemStack(Material.INK_SACK);
		im = is.getItemMeta();
		is.setDurability((short) (t.getTargetType().equals(TargetEnum.CLOSEST) ? 10 : 8)); //Green or gray, depending on whether the tower is set to it
		im.setDisplayName(ChatColor.YELLOW + "Attack Closest");
		is.setItemMeta(im);
		
		inv.setItem(9, is);

		is.setDurability((short) (t.getTargetType().equals(TargetEnum.STRONGEST) ? 10 : 8));
		im.setDisplayName(ChatColor.YELLOW + "Attack Strongest");
		is.setItemMeta(im);
		
		inv.setItem(18, is);
		
		is.setDurability((short) (t.getTargetType().equals(TargetEnum.FIRST) ? 10 : 8));
		im.setDisplayName(ChatColor.YELLOW + "Attack First");
		is.setItemMeta(im);
		
		inv.setItem(27, is);
		
		//Upgrades
		if(t.getUpgradeLevel(1) < 3 && (t.getUpgradeLevel(1) < 2 || t.getUpgradeLevel(2) < 3))
		{
			is = new ItemStack(Material.GOLD_BLOCK);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Upgrade to " + ChatColor.GREEN + "Level " + (t.getUpgradeLevel(1) + 1) + ChatColor.YELLOW + ": " + t.getName(t.getUpgradeLevel(1) + 1));
			im.setLore(Arrays.asList(new String[]{
					ChatColor.GRAY + t.getDescription(t.getUpgradeLevel(1) + 1),
					ChatColor.YELLOW + "Costs " + (int) (25 * Math.pow(2, t.getUpgradeLevel(1) + 1) * t.getMultiplier()) + " essence"
			}));
			is.setItemMeta(im);

			inv.setItem(12, is);
		}
		
		if(t.getUpgradeLevel(2) < 3 && (t.getUpgradeLevel(1) < 3 || t.getUpgradeLevel(2) < 2))
		{
			is = new ItemStack(Material.GOLD_BLOCK);
			im = is.getItemMeta();
			im.setDisplayName(ChatColor.YELLOW + "Upgrade to " + ChatColor.GREEN + "Level " + (t.getUpgradeLevel(2) + 1) + ChatColor.YELLOW + ": " + t.getName(t.getUpgradeLevel(2) + 4));
			im.setLore(Arrays.asList(new String[]{
					ChatColor.GRAY + t.getDescription(t.getUpgradeLevel(2) + 4),
					ChatColor.YELLOW + "Costs " + (int) (25 * Math.pow(2, t.getUpgradeLevel(2) + 1) * t.getMultiplier()) + " essence"
			}));
			is.setItemMeta(im);

			inv.setItem(14, is);
		}
		
		is = new ItemStack(Material.REDSTONE_BLOCK);
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.YELLOW + "Sell this tower");
		im.setLore(Arrays.asList(new String[]{ChatColor.GREEN + "Will return " + ChatColor.YELLOW + ((int) (0.75 * t.getValue())) + ChatColor.GREEN + " essence"}));
		is.setItemMeta(im);
		inv.setItem(26, is);
		
		p.openInventory(inv);
	}

	/***
	 * Creates a custom GUI inventory for a player. Stained clay holds coordinate data. 
	 * @param p - Player creating the tower
	 * @param pd - Data for player creating the tower
	 * @param b - Block on which the player is attempting to build the tower
	 * @param d - Data value for block color
	 */
	private void createTowerGUI(Player p, PlayerData pd, Block b, byte d)
	{
		//Inventory
		Inventory inv = Bukkit.createInventory(p, 27, "Build Tower"); //Creates 9x3 inventory

		//Definitions
		ItemStack is = new ItemStack(Material.HARD_CLAY); //First item: stained clay
		ItemMeta im = is.getItemMeta(); //Fetch basic item metadata

		//First item: Placement data
		is.setDurability(d); //Data value for the block's color
		im.setDisplayName(b(pd.getData()) + "Tower Placement:");
		//This item indicates the position of the tower and is used for my convenience by storing the placement of the tower in question
		im.setLore(Arrays.asList(new String[]{ChatColor.GRAY + "" + b.getX(), ChatColor.GRAY + "" + b.getY(), ChatColor.GRAY + "" + b.getZ()}));
		is.setItemMeta(im);

		inv.setItem(4, is); //Middle of first row

		//Second item: Archer tower
		is = new ItemStack(Material.ARROW);
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.GRAY + arrowTowerGlobal.getName(0)); //Index 0 holds information on the tower itself
		im.setLore(Arrays.asList(new String[]{ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + Math.round(arrowTowerGlobal.getMultiplier() * 250), ChatColor.DARK_PURPLE + arrowTowerGlobal.getDescription(0)})); //Ditto
		is.setItemMeta(im);
		inv.setItem(10, is); //Second slot, second row

		//Third item: Priest tower
		is = new ItemStack(Material.BOOK);
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.GRAY + priestTowerGlobal.getName(0)); //Ditto
		im.setLore(Arrays.asList(new String[]{ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + Math.round(priestTowerGlobal.getMultiplier() * 250), ChatColor.DARK_PURPLE + priestTowerGlobal.getDescription(0)})); //Ditto
		is.setItemMeta(im);
		inv.setItem(12, is); //Fourth slot, second row

		//Fourth item: Tesla tower
		is = new ItemStack(Material.BLAZE_ROD);
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.GRAY + teslaTowerGlobal.getName(0)); //Etc.
		im.setLore(Arrays.asList(new String[]{ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + Math.round(teslaTowerGlobal.getMultiplier() * 250), ChatColor.DARK_PURPLE + teslaTowerGlobal.getDescription(0)}));
		is.setItemMeta(im);
		inv.setItem(14, is); //Sixth slot, second row

		//Fifth item: Dragonbreath tower
		is = new ItemStack(Material.DRAGONS_BREATH);
		im = is.getItemMeta();
		im.setDisplayName(ChatColor.GRAY + dragonTowerGlobal.getName(0));
		im.setLore(Arrays.asList(new String[]{ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + Math.round(dragonTowerGlobal.getMultiplier() * 250), ChatColor.DARK_PURPLE + dragonTowerGlobal.getDescription(0)}));
		is.setItemMeta(im);
		inv.setItem(16, is); //Eigth slot, second row
		p.openInventory(inv);
	}
	
	//Checks whether a tower at a certain location intersects any other towers
	//Could be somewhat resource-intensive, but is relatively rarely called.
	private boolean towerIntersects(Tower tower, Location loc)
	{
		int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ(); //Fewer function calls
		for(PlayerData pd : playerData.values())
			for(Tower t : pd.getTowers())
			{
				for(CoordSet cs : t.getStructure().getStructure().keySet())
					for(CoordSet cs2 : tower.getStructure().getStructure().keySet())
						if(t.getLocation().getX() + cs.getX() == x + cs2.getX() && t.getLocation().getY() + cs.getY() == y + cs2.getY() && t.getLocation().getZ() + cs.getZ() == z + cs2.getZ())
							return true;
			}
		return false;
	}
	
	/***Helper function to make code more readable, gets distance between location and coordset
	 */
	private double a(Location loc, CoordSet cs)
	{
		return Math.sqrt(Math.pow(loc.getX() - cs.getX(), 2) + Math.pow(loc.getY() - cs.getY(), 2) + Math.pow(loc.getZ() - cs.getZ(), 2));
	}
	
	/***Helper function to associate color data with player number
	 * @param i - Player number (0-3) to associate color value
	*/
	private int a(int i)
	{
		switch(i)
		{
			case 0:
				return 4;
			case 1:
				return 14;
			case 2:
				return 11;
			case 3:
				return 13;
			default:
				return 13;
		}
	}
	
	/***Helper function to associate color code with color data
	 * @param i - PlayerData data field
	*/
	private ChatColor b(int i)
	{
		switch(i)
		{
			case 0:
				return ChatColor.WHITE;
			case 1:
				return ChatColor.GOLD;
			case 2:
				return ChatColor.LIGHT_PURPLE;
			case 3:
				return ChatColor.BLUE;
			case 4:
				return ChatColor.YELLOW;
			case 5:
				return ChatColor.GREEN;
			case 6:
				return ChatColor.LIGHT_PURPLE;
			case 7:
				return ChatColor.DARK_GRAY;
			case 8:
				return ChatColor.GRAY;
			case 9:
				return ChatColor.AQUA;
			case 10:
				return ChatColor.DARK_PURPLE;
			case 11:
				return ChatColor.BLUE;
			case 12:
				return ChatColor.GOLD;
			case 13:
				return ChatColor.GREEN;
			case 14:
				return ChatColor.RED;
			case 15:
				return ChatColor.BLACK;
			default:
				return ChatColor.WHITE;
		}
	}
	
	/***
	 * Hides or shows all players in the spectator list
	 * @param b - True to hide, false to unhide
	 */
	public void hideUnhideSpectators(boolean b)
	{
		if(b)
		{
			for(Player p : Bukkit.getOnlinePlayers())
				for(UUID uid : spectators)
					if(Bukkit.getPlayer(uid).isOnline())
						p.hidePlayer(Bukkit.getPlayer(uid));
		} else
			for(Player p : Bukkit.getOnlinePlayers())
				for(Player p2 : Bukkit.getOnlinePlayers())
					p.showPlayer(p);
	}
	
	/***
	 * Gets whichever tower the selected block is inside, if any.
	 * @param pd - PlayerData with towers to cycle through.
	 * @param b - Block to be checked.
	 * @return Tower containing block, if any, null otherwise.
	 */
	private Tower blockInsideTower(PlayerData pd, Block b)
	{
		if(pd == null || b == null)
			return null;
		for(Tower t : pd.getTowers())
		{
			if(t.getLocation().getWorld().equals(b.getWorld()))
				for(CoordSet cs : t.getStructure().getStructure().keySet())
				{
					Location loc = t.getLocation();
					if(loc.getX() + cs.getX() == b.getX() && loc.getY() + cs.getY() == b.getY() && loc.getZ() + cs.getZ() == b.getZ())
						return t;
				}
		}
		return null;
	}
	
	private void startTDGame()
	{
		setWorld("spawn", 0);
		state = GameState.SPAWN;
		for(PlayerData pd : playerData.values())
			for(Tower t : pd.getTowers())
				t.destroyTower();
		Processor.clearAll();
		playerData.clear();
	}
	
	//These methods are necessary because Java is unable to handle normal code.
	private void makeArrowGlobal()
	{
		arrowTowerGlobal = new ArrowTower(new Schematic(new File(pluginPath + "TowerData" + File.separator + "arrow.schem")), null);
	}
	
	private void makePriestGlobal()
	{
		priestTowerGlobal = new PriestTower(new Schematic(new File(pluginPath + "TowerData" + File.separator + "priest.schem")), null);
	}
	
	private void makeTeslaGlobal()
	{
		teslaTowerGlobal = new TeslaTower(new Schematic(new File(pluginPath + "TowerData" + File.separator + "tesla.schem")), null);
	}
	
	private void makeDragonGlobal()
	{
		dragonTowerGlobal = new DragonTower(new Schematic(new File(pluginPath + "TowerData" + File.separator + "dragon.schem")), null);
	}
	
	//Debug commands
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args)
	{
		//If the player traps himself in a tower
		if(cmd.getName().equalsIgnoreCase("stuck") && state.equals(GameState.GAME_ACTIVE) && sender instanceof Player && playerData.containsKey(((Player) sender).getUniqueId()))
		{
			Player p = (Player) sender;
			PlayerData pd = playerData.get(p.getUniqueId());
			p.teleport(new Location(currentWorld, pd.getStartLoc().getX(), pd.getStartLoc().getY(), pd.getStartLoc().getZ()));
		}
		if(!sender.isOp())
			return false;
		if(cmd.getName().equalsIgnoreCase("debug-tower"))
		{
			progress = 1;
		} else if(cmd.getName().equalsIgnoreCase("debug-export") && args.length == 1)
		{
			if(center == null || corner1 == null || corner2 == null)
				return false;
			Schematic s = new Schematic(); 
			s.generateSchematic(center, corner1, corner2);
			try 
			{
				s.saveSchematic(new File(pluginPath + "TowerData" + File.separator + args[0] + ".schem"));
			} catch (IOException e) { e.printStackTrace(); }
		} else if(cmd.getName().equalsIgnoreCase("debug-build") && args.length == 1 && sender instanceof Player)
		{
			Player p = (Player) sender;
			if(args[0].equalsIgnoreCase("arrow"))
				arrowTowerGlobal.buildTower(p.getLocation());
			else if(args[0].equalsIgnoreCase("priest"))
				priestTowerGlobal.buildTower(p.getLocation());
			else if(args[0].equalsIgnoreCase("tesla"))
				teslaTowerGlobal.buildTower(p.getLocation());
			else if(args[0].equalsIgnoreCase("dragon"))
				dragonTowerGlobal.buildTower(p.getLocation());
		} else if(cmd.getName().equalsIgnoreCase("debug-active"))
		{
			startTDGame();
		} else if(cmd.getName().equalsIgnoreCase("debug-start"))
		{
			startCountdown(10);
		}
				
		return true;
	}
	
}