package kian.starjet.core;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import kian.starjet.core.WorldData.WorldType;
import kian.starjet.ship.CapitalShip;
import kian.starjet.ship.Ship;
import kian.starjet.ship.Ship.ShipType;
import kian.starjet.ship.Turret;
import kian.starjet.util.Schematic;
import kian.starjet.util.ZipUtils;
import net.md_5.bungee.api.ChatColor;
import thekian.nms.protocol.Packets;
import thekian.nms.protocol.Titles;

/*
Current classes:
	
attacks
	ModelAttack.java - Complete
	ParticleBeam.java - Complete
	ParticleProjectile.java - Complete
	ParticleSpell.java - Complete
	ParticleTracing.java - Complete
	ParticleTracingSpell.java - Complete
core
	BasicEvents.java
	CoordSet.java
	GameState.java
	Messages.java
	PlayerData.java
	Processor.java
	StarjetMain.java
	Team.java - Complete
	WorldData.java
ship
	AttackData.java
	AttackType.java - Complete
	CapitalShip.java
	Ship.java
	Turret.java
util
	BlockData.java - Complete
	Matrix.java - Complete
	Schematic.java - Complete
	ZipUtils.java - Complete

*/

/***
 * A space fighter battle in Minecraft. There are two modes: dreadnought assault and deathmatch. The former involves
 * damaging massive dreadnoughts belonging to each team, with the first team to gather enough points by killing enemies and
 * attacking the dreadnought's hardpoints winning. The deathmatch mode is simpler, requiring merely gathering enough kills to win.
 *
 * Weapons in the plugin fall into two categories: particle weapons and 3D model weapons. The former involve custom particle effects
 * which damage ships when they enter their custom collision box (a different manner of detection than in previous plugins). The latter
 * are armor stands with custom 3D models as helmets which, upon getting close enough to another ship in the same manner as the particle
 * weapons, cause damage.
 * 
 * @author TheKian
 *
 */
public class StarjetMain extends JavaPlugin implements Listener 
{
	private Plugin plugin;
	private String serverPath, pluginPath;
	private List<UUID> spectators = new ArrayList<UUID>(); //Spectators get hidden during the game
	private List<WorldData> worldData = new ArrayList<WorldData>(); //Contains world data for each world
	private HashMap<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>(); //Holds metadata for each player
	private List<CapitalShip> dreadnoughts = new ArrayList<CapitalShip>();
	private Schematic dreadSchem, frigSchem;

	private HashMap<UUID, ShipType> preferences = new HashMap<UUID, ShipType>();
	
	final String[] TIPS = {ChatColor.GREEN + "Starjet Interceptors" + ChatColor.GRAY + " are light and maneuverable, with fast-firing weapons. They are fragile, however.",
			ChatColor.GREEN + "Viper Fighters" + ChatColor.GRAY + " have powerful lance cannons, but no secondary armaments. They can fulfill a number of roles.",
			ChatColor.GREEN + "Marauder Bombers" + ChatColor.GRAY + " are heavily armored and repair themselves quickly. Their torpedoes are powerful against stationary targets, but their beam cannons have low range and poor damage."};
	
	final int DEFAULT_START = 30;
	final int FILLED_START = 10;
	final int[] ALERT_TIMES = {30, 20, 10, 5, 4, 3, 2, 1};
	final int FILLED_PLAYERS = 16;
	final int MIN_PLAYERS = 2;
	final int MIN_PLAYERS_END = 0;
	final int MAX_KILLS_DM = 25;
	final int MAX_POINTS_DND = 200;
	
	private String[] places = {null, null, null};
	
	private CoordSet corner1, corner2; int progress = 0; Location center; //Unsophisticated way to export ships, borrowed from TD

	private GameModeState mode; //Current game mode.
	private GameState state; //State in which the game is at present
	private World currentWorld; //World which is currently loaded
	private int currentData; //Index of loaded world

	@Override
	public void onEnable()
	{
		Bukkit.getServer().getPluginManager().registerEvents(this, this); //The main plugin handles all events related to the actual function of the minigame
		Bukkit.getServer().getPluginManager().registerEvents(new BasicEvents(this), this); //Meanwhile, the basic events primarily just disable unwanted events
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

		//Folders with necessary data
		if(!(new File(pluginPath + "StarjetWorldData")).exists()) //Contains WorldData sources
		{
			new File(pluginPath + "StarjetWorldData").mkdirs();
		}
		if(!(new File(pluginPath + "CapitalData")).exists()) //Contains capital ship schematics
		{
			new File(pluginPath + "CapitalData").mkdirs();
		}
		if(!(new File(serverPath + "spawn")).exists()) //Makes sure that there is a spawn world folder available
		{
			new File(serverPath + "spawn").mkdirs();
		}
		//Log the server and plugin path to the console so we know that the server is looking in the right location
		Bukkit.getLogger().info("Server path: " + serverPath);
		Bukkit.getLogger().info("Plugin path: " + pluginPath);

		Bukkit.getLogger().info("Attempting to load " + pluginPath + "StarjetWorldData" + File.separator + "spawn.dat");
		//Default spawn loader. If there is no spawn data, the minigame will likely not work correctly.
		worldData.add(new WorldData(new File(pluginPath + "StarjetWorldData" + File.separator + "spawn.dat"), true));

		dreadSchem = new Schematic(new File(pluginPath + "CapitalData" + File.separator + "dread.schem"));
		frigSchem = new Schematic(new File(pluginPath + "CapitalData" + File.separator + "frigate.schem"));
		
		
		//Load the rest of the world data
		for(File f : (new File(pluginPath + "StarjetWorldData")).listFiles())
		{
			if(!f.getName().equalsIgnoreCase("spawn.dat"))
			{
				Bukkit.getLogger().info("Attempting to load " + f.getPath());
				worldData.add(new WorldData(f, false));
			}
		}		
		
		//Actually load the worlds
		for(int i = 0; i < worldData.size(); i++)
			loadWorld(i);
		
		//Ship regen and once per second timers
		new BukkitRunnable(){
			
			public void run()
			{
				if(state.equals(GameState.GAME_ACTIVE))
				{
					//Execute regen
					for(PlayerData pd : playerData.values())
						if(pd.getShip().getHealth() > 0 && !pd.getShip().getRespawning()) //If the ship is actually alive
							pd.getShip().regen();
					if(mode.equals(GameModeState.DEF_AND_DET))
					{
						if(Team.BLUE.getPoints() > MAX_POINTS_DND || Team.RED.getPoints() > MAX_POINTS_DND 
								|| Team.GREEN.getPoints() > MAX_POINTS_DND || Team.YELLOW.getPoints() > MAX_POINTS_DND)
						{
							broadcastEndgame();
							resetPluginInstance(GameState.GAME_ENDED);
							new BukkitRunnable(){
								
								public void run()
								{
									startStarjet();
								}
								
							}.runTaskLater(plugin, 100);
						}
					}
				}
			}
			
		}.runTaskTimer(plugin, 0, 20);
		
		//Keep the time at midnight when the game is active and at noon when in spawn
		new BukkitRunnable(){

			public void run()
			{
				if(!state.equals(GameState.DISABLED) && currentWorld != null)
					if(state.equals(GameState.SPAWN))
						currentWorld.setTime(6000);
					else
						currentWorld.setTime(18000);
			}
			
		}.runTaskTimer(plugin, 10, 10);
		
		//HUD and 5 times per second timers
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
							String s = "";
							if(pd.getShip() != null && !(pd.getShip().getRespawning() || pd.getShip().getHealth() <= 0))
							{
								s = ChatColor.RED + "Health: " + ChatColor.YELLOW + pd.getShip().getHealth();
								if (pd.getShip().getPrimaryWeapon() != null)
									s += " >>> " + ChatColor.RED + "Main Weapon: " + ChatColor.YELLOW + (pd.getShip().getCoolOne() > 0 ? new DecimalFormat("#0.0").format(pd.getShip().getCoolOne() / 20.0) : "Ready!");
								if(pd.getShip().getSecondaryWeapon() != null)
									s += " >>> " + ChatColor.RED + "Secondary Weapon: " + ChatColor.YELLOW + (pd.getShip().getCoolTwo() > 0 ? new DecimalFormat("#0.0").format(pd.getShip().getCoolTwo() / 20.0) : "Ready!");
							} else
							{
								s = ChatColor.RED + "Respawning.";
							}
							Packets.sendPacket(p, Titles.createHotbarPacket(s));
						}
					}
				}
			}
		}.runTaskTimer(plugin, 10, 4);
		
		//Ship movement runnable and 10 times per second timers
		new BukkitRunnable(){
			
			public void run()
			{
				if(state.equals(GameState.GAME_ACTIVE))
				{
					for(PlayerData pdat : playerData.values())
					{
						//Check if the ship is actually alive
						if(pdat.getShip() != null && pdat.getShip().getHealth() < 0 && !pdat.getShip().getRespawning())
						{
							Ship damager = pdat.getShip().killShip();
							PlayerData killer = damager == null ? null : getPlayerDataFromShip(damager);
							if(killer != null)
							{
								killer.addKills(1);
								killer.getPlayer().sendMessage(Messages.STARJET + "" + ChatColor.YELLOW + "You killed " + ChatColor.GREEN + pdat.getPlayer().getName() + ChatColor.YELLOW + ".");
								//Make an explosion effect where the player died
								pdat.getPlayer().getWorld().createExplosion(pdat.getPlayer().getLocation(), 0);
								setSpectator(pdat.getPlayer());
								//Respawn the player 5 seconds later.
								new BukkitRunnable(){
									
									public void run()
									{
										if(state.equals(GameState.GAME_ACTIVE))
										{
											if(pdat == null)
												return;
											Player p = pdat.getPlayer();
											if(p.isOnline())
											{
												//Unspectate him
												removeSpectator(p);
												pdat.getShip().generateShipModel(pdat.getStartLoc().toLocation(currentWorld));
												pdat.getShip().getShipModel().addPassenger(pdat.getPlayer());
											}
										}
									}
									
								}.runTaskLater(plugin, 100);
								
								if(killer.getKills() > MAX_KILLS_DM && mode.equals(GameModeState.DEATHMATCH))
								{
									state = GameState.GAME_ENDED;
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
											if(!pd.getShip().getRespawning())
											{
												places[0] = pd.getPlayer().getName();
												setSpectator(pd.getPlayer());
											}
									new BukkitRunnable(){
										
										public void run()
										{
											startStarjet();
										}
										
									}.runTaskLater(plugin, 100);	
								}
								else if(state.equals(GameModeState.DEF_AND_DET))
									pdat.getTeam().addPoints(damager instanceof Turret ? 5 : 10);
							}
							continue; //Ship past the rest of the loop for that player.
						}
						//Process a bunch of ship movement math
						if(pdat.getShip() != null && pdat.getShip().getShipModel() != null)
						{
							//If out of bounds kill player
							if(!pdat.getShip().getRespawning() && outOfBounds(pdat.getShip()))
							{
								pdat.getShip().killShip();
								//Respawn him
								new BukkitRunnable(){
									
									public void run()
									{
										if(state.equals(GameState.GAME_ACTIVE))
										{
											if(pdat == null)
												return;
											Player p = pdat.getPlayer();
											if(p.isOnline())
											{
												//Unspectate him
												removeSpectator(p);
												pdat.getShip().generateShipModel(pdat.getStartLoc().toLocation(currentWorld));
												pdat.getShip().getShipModel().addPassenger(pdat.getPlayer());
											}
										}
									}
									
								}.runTaskLater(plugin, 100);
								continue;
							}
							Ship ship = pdat.getShip(); //Make code more readable
							ArmorStand model = ship.getShipModel();
							//Adds a vector in the direction that the player is looking multiplied by the acceleration over 0.1 seconds to the ship's current velocity
							Vector v = ship.getVelocity() != null ? ship.getVelocity().clone().add(pdat.getPlayer().getLocation().getDirection().multiply(ship.getAcceleration() / 10)) : pdat.getPlayer().getLocation().getDirection().multiply(ship.getAcceleration() / 10);
							
							if(v.length() > ship.getMaxSpeed()) //Limit to the max speed of the ship
								v = v.normalize().multiply(ship.getMaxSpeed()); //Technically setting v equal to it should be unnecessary, but Bukkit behaves strangely sometimes.
							model.setGravity(true); //Due to bizarre problems Bukkit has
							model.setVelocity(v);
							ship.setVelocity(v);
							Vector curDirCheck = pdat.getCurrentDirection() != null ? pdat.getCurrentDirection() : null;
							//If the two vectors are parallel, e.g. the ship is moving in the same direction still, their length is 0 (length squared is a faster calculation and also returns 0) and 
							if(curDirCheck != null && curDirCheck.getCrossProduct(v).lengthSquared() != 0)
							{
								//A bunch of trig to pose the model
								model.setHeadPose(new EulerAngle(Math.toDegrees(Math.atan(v.getY() / Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getZ(), 2)))),
										Math.toDegrees(Math.atan(v.getX() / v.getZ())),
										0));
								
								//This one is probably wrong
								/*model.setHeadPose(new EulerAngle(Math.toDegrees(Math.atan(v.getZ() / v.getX())),
										Math.toDegrees(Math.atan(v.getY() / Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getZ(), 2)))),
										Math.toDegrees(Math.atan(v.getX() / v.getZ()))));*/
								pdat.setCurrentDirection(v.clone());
							}
						}
						//Do cooldown. Cooldown is in increments of 2 ticks.
						if(pdat.getShip() != null)
							pdat.getShip().cooldown();
					}
					//Do cooldown for any dreadnoughts
					for(CapitalShip cs : dreadnoughts)
					{
						if(cs.getHealth() <= 0 && cs.isAlive())
						{
							cs.killShip();
							continue;
						}
						for(Turret t : cs.getTurrets())
						{
							if(t.getRespawning())
								continue;
							if(t.getHealth() <= 0 && !t.getRespawning())
							{
								Ship s = t.killShip();
								PlayerData pd = getPlayerDataFromShip(s);
								if(pd != null)
									pd.getPlayer().sendMessage(Messages.STARJET + "" + ChatColor.YELLOW + "You killed " + ChatColor.GREEN + "a turret" + ChatColor.YELLOW + ".");
								continue;
							}
							t.cooldown();
							if(t.getTargeting())
								for(CapitalShip cs2 : dreadnoughts)
									if(!cs2.getTeam().equals(cs.getTeam()))
									{
										t.attackCapitalShip(cs2);
									}
									else
										t.attackEnemy(Processor.getTargets());
						}
					}
				}
			}
			
		}.runTaskTimer(plugin, 0, 2);

		
		//Run processor and other once per tick timers
		new BukkitRunnable(){
			
			public void run()
			{
				if(state.equals(GameState.GAME_ACTIVE))
				{
					Processor.process(plugin);
				}
			}
			
		}.runTaskTimer(plugin, 10, 1);
		
	}
	
	public void setWorld(String name, int index)
	{
		currentWorld = Bukkit.getServer().getWorld(name);
		currentData = index;
		WorldData currentWorldData = worldData.get(index);
		System.out.println(currentWorldData.getWorldType());
		if(currentWorldData.getWorldType().equals(WorldType.SPAWN))
		{
			startTips();
			CoordSet cs = currentWorldData.getStartLocations().get(0);
			for(Player p : Bukkit.getOnlinePlayers())
			{
				p.closeInventory();
				p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
				p.setInvulnerable(true);
				p.setCanPickupItems(false);
				p.setAllowFlight(false);
				p.resetMaxHealth();
				p.getInventory().clear();
				p.setGameMode(GameMode.SURVIVAL);
				p.removePotionEffect(PotionEffectType.NIGHT_VISION);
				
				ItemStack is = new ItemStack(Material.COMPASS);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(ChatColor.YELLOW + "Select Ship");
				is.setItemMeta(im);
				p.getInventory().setItem(0, is); //Add ship selector to first inventory slot.
				
				new BukkitRunnable(){ //Extra delayed teleport to teleport player to the correct world location 
					
					public void run()
					{
						p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
					}
					
				}.runTaskLater(plugin, 2);
				startTips();
			}
		} else if(currentWorldData.getWorldType().equals(WorldType.GAMEWORLD))
		{
			System.out.println("Gameworld start.");
			playerData.clear();
			int metaValue = 0;
			//For the sake of testing, the gamemode here will always be defend and destroy
			//Given that deathmatch requires multiple players and the ships are always on the move
			mode = GameModeState.DEF_AND_DET;
			for(CoordSet cs : currentWorldData.getDreadnoughtSpawns())
			{
				//Capital ships have different data files for the different teams to make sure there are only main cannons on the proper side.
				CapitalShip dread = new CapitalShip(new File(pluginPath + "CapitalData" + File.separator + (metaValue % 2 == 0 ? "dreadBlue.dat" : "dreadRed.dat")));
				dread.setSchematic(dreadSchem, metaValue % 2 == 0 ? 11 : 14);
				dread.build(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()));
				dread.generateTurrets();
				dread.setTeam(metaValue % 2 == 0 ? Team.BLUE : Team.RED);
				dreadnoughts.add(dread);
				metaValue++;
			}
			metaValue = 0;
			for(CoordSet cs : currentWorldData.getFrigateSpawns())
			{																				//Frigates don't need team specific data
				CapitalShip frigate = new CapitalShip(new File(pluginPath + "CapitalData" + File.separator + "frigate.dat"));//(metaValue % 2 == 0 ? "frigBlue.dat" : "frigRed.dat")));
				frigate.setSchematic(frigSchem, metaValue % 2 == 0 ? 11 : 14);
				frigate.build(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()));
				frigate.generateTurrets();
				frigate.setTeam(metaValue % 2 == 0 ? Team.BLUE : Team.RED);
				dreadnoughts.add(frigate);
				
				metaValue++;
			}
			for(Player p : Bukkit.getOnlinePlayers())
			{
				PlayerData pd = null;
				if(metaValue < 16)
				{
					if(metaValue % 2 == 0)
						pd = new PlayerData(currentWorldData.getStartLocations().get(0), Team.BLUE, p);
					else
						pd = new PlayerData(currentWorldData.getStartLocations().get(1), Team.RED, p);
					playerData.put(p.getUniqueId(), pd);
					p.closeInventory();
					
					//Weapon control tool
					ItemStack is = new ItemStack(Material.EMERALD);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName(ChatColor.RED + "Right Click - " + ChatColor.YELLOW + "Main Weapon " + ChatColor.RED + "| Left Click - " + ChatColor.YELLOW + "Secondary Weapon");
					is.setItemMeta(im);
					p.getInventory().setItem(0, is);
				}
				CoordSet cs = pd == null ? currentWorldData.getSpectatorStartLocation() : pd.getStartLoc();
				metaValue++;
				p.setInvulnerable(true);
				p.setCanPickupItems(false);
				p.setAllowFlight(false);
				p.setGameMode(GameMode.SURVIVAL);
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 0)); //Allow the player to see in the dark map
				p.resetMaxHealth();
				p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);

				final int val = metaValue;
				//Set ship
				new BukkitRunnable(){ //Extra delayed teleport to make sure player ends up in the correct world location 

					public void run()
					{
						p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()), TeleportCause.PLUGIN);
						if(playerData.containsKey(p.getUniqueId()))
						{
							PlayerData pdat = playerData.get(p.getUniqueId());
							System.out.println("Does preferences contain player: " + preferences.containsKey(p.getUniqueId()));
							if(preferences.containsKey(p.getUniqueId()))
								System.out.println("Ship type: " + preferences.get(p.getUniqueId()).toString());
							pdat.setShip(preferences.containsKey(p.getUniqueId()) ? preferences.get(p.getUniqueId()) : ShipType.VIPER);
							pdat.getShip().setTeam(val % 2 == 0 ? Team.BLUE : Team.RED);
							ArmorStand as = pdat.getShip().generateShipModel(p.getLocation());
							as.addPassenger(p);
							as.setGravity(true);
						}
					}

				}.runTaskLater(plugin, 2);
			}
			preferences.clear();
		}
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
			}
			System.out.println("Setting game world.");
			int index = (int) (Math.random() * (worldData.size() - 1)) + 1;
			setWorld(worldData.get(index).getWorldName(), index);
			state = GameState.GAME_ACTIVE;
			hideUnhideSpectators(true);
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
	
	/***
	 * Starts cylcing through tips so long as the game state is in spawn.
	 */
	private void startTips()
	{
		new BukkitRunnable(){
			
			public void run()
			{
				if(state.equals(GameState.SPAWN))
				{
					for(Player p : Bukkit.getOnlinePlayers())
					{
						p.sendMessage(Messages.TIPS.getValue() + TIPS[(int) (Math.random() * TIPS.length)]);	
					}
					startTips();
				}
			}
			
		}.runTaskLater(plugin, 80 * (int) (Math.random() * 8 + 8));
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
	
	//Unhides all players and then rehides spectators
	public void reloadSpectators()
	{
		hideUnhideSpectators(false);
		hideUnhideSpectators(true);
	}
	
	public void setSpectator(Player p)
	{
		p.setCollidable(false);
		p.setInvulnerable(true);
		spectators.add(p.getUniqueId());
		p.setGameMode(GameMode.SURVIVAL);
		if(state.equals(GameState.GAME_ACTIVE))
		{
			CoordSet cs = worldData.get(currentData).getSpectatorStartLocation();
			if(cs == null)
				p.teleport(new Location(currentWorld, 0, 10, 0));
			else
				p.teleport(new Location(currentWorld, cs.getX(), cs.getY(), cs.getZ()));
		}
		hideUnhideSpectators(true);
	}
	
	public void removeSpectator(Player p)
	{
		p.setCollidable(true);
		spectators.remove(p.getUniqueId());
		reloadSpectators();
	}
		
	//Prevent players from leaving their ships
	@EventHandler
	public void onDismount(VehicleExitEvent e)
	{
		if(state.equals(GameState.GAME_ACTIVE) && e.getExited() instanceof Player)
		{
			e.setCancelled(true);
			new BukkitRunnable(){
				
				public void run()
				{
					if(!e.getVehicle().getPassengers().contains(e.getExited())) //In case cancelling the event doesn't work correctly, as is the case with FoodLevelChangeEvent.
						e.getVehicle().addPassenger(e.getExited());
				}
				
			}.runTaskLater(plugin, 1);
		}
	}
	
	//Handles the players' interactions for weapon firing
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		
		//#####################################################################################
		//#	Borrowed from Tower Defense for capital ship schematics.																				  #
		//# Non-game related code section - used for exporting ship schematics from in-game.  #
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

		
		if(state.equals(GameState.GAME_ACTIVE))
		{
			e.setCancelled(true);
			if(playerData.containsKey(e.getPlayer().getUniqueId()))
			{
				PlayerData pd = playerData.get(e.getPlayer().getUniqueId());
				if(pd.getShip() != null && pd.getShip().isAlive())
					if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
						pd.getShip().attackPrimary();
					else if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))
						pd.getShip().attackSecondary();
			}
		} else if(state.equals(GameState.SPAWN))
		{
			e.setCancelled(true);
			Player p = e.getPlayer();
			ItemStack held = p.getInventory().getItem(p.getInventory().getHeldItemSlot()); 
			if(held == null || held.getType().equals(Material.AIR) || held.getItemMeta() == null)
				return;
			if(held.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Select Ship"))
			{
				new BukkitRunnable(){

					public void run()
					{
						//Create inventory GUI for ship selection
						Inventory inv = Bukkit.createInventory(p, 9, "Select Ship");

						ItemStack is = new ItemStack(Material.ARROW);
						ItemMeta im = is.getItemMeta();
						im.setDisplayName(ChatColor.YELLOW + "Starjet Interceptor");
						is.setItemMeta(im);
						inv.setItem(2, is);

						is = new ItemStack(Material.IRON_SWORD);
						im = is.getItemMeta();
						im.setDisplayName(ChatColor.YELLOW + "Viper Fighter");
						is.setItemMeta(im);
						inv.setItem(4, is);

						is = new ItemStack(Material.TNT);
						im = is.getItemMeta();
						im.setDisplayName(ChatColor.YELLOW + "Marauder Bomber");
						is.setItemMeta(im);
						inv.setItem(6, is);

						p.openInventory(inv);
					}

				}.runTaskLater(plugin, 1); //Run later to avoid problems the plugin has had
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if(!state.equals(GameState.DISABLED))
			e.setCancelled(true);
		if(e.getInventory().getName().equals("Select Ship") && state.equals(GameState.SPAWN))
		{
			if(e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR))
			{
				ItemStack is = e.getCurrentItem();
				Player p = (Player) e.getWhoClicked(); //Never not a player
				if(is.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Starjet Interceptor"))
				{
					System.out.println("Selected starjet.");
					preferences.remove(p.getUniqueId()); //Remove if present
					preferences.put(p.getUniqueId(), ShipType.STARJET);
				} else if(is.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Viper Fighter"))
				{
					System.out.println("Selected viper.");
					preferences.remove(p.getUniqueId());
					preferences.put(p.getUniqueId(), ShipType.VIPER);
				} else if(is.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Marauder Bomber"))
				{
					System.out.println("Selected marauder.");
					preferences.remove(p.getUniqueId());
					preferences.put(p.getUniqueId(), ShipType.MARAUDER);
				}
				System.out.println("Did it work: " + preferences.containsKey(p.getUniqueId()));
				p.closeInventory();
			}
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
	
	/***
	 * Gets the associated player data for a given ship.
	 * @param s - Ship associated with player data.
	 * @return - Owner of the ship if one exists, otherwise null.
	 */
	private PlayerData getPlayerDataFromShip(Ship s)
	{
		if(s == null)
			return null;
		for(PlayerData pd : playerData.values())
			if(pd.getShip().equals(s))
				return pd;
		return null;
	}
	
	public enum GameModeState
	{
		DEATHMATCH, DEF_AND_DET;
	}	

	public void resetPluginInstance(GameState state)
	{
		for(Player p : Bukkit.getOnlinePlayers())
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		this.state = state;
		Team.BLUE.reset();
		Team.YELLOW.reset();
		Team.GREEN.reset();
		Team.RED.reset();
	}
	
	private void startStarjet()
	{
		setWorld("spawn", 0);
		state = GameState.SPAWN;
		Processor.clearAll();
		playerData.clear();
	}
	
	private void broadcastEndgame()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(mode.equals(GameModeState.DEATHMATCH))
			{
				p.sendMessage(Messages.BORDER.getValue());
				p.sendMessage(" ");
				p.sendMessage((places[0] == null) ? ChatColor.YELLOW + "        First place: " + ChatColor.GRAY + "Nobody" : ChatColor.YELLOW + "        First place:" + ChatColor.GREEN + places[0] + ChatColor.YELLOW + ", " + ChatColor.GREEN + playerData.get(Bukkit.getPlayer(places[0])).getKills() + ChatColor.YELLOW + " kills.");
				p.sendMessage((places[1] == null) ? ChatColor.YELLOW + "       Second place: " + ChatColor.GRAY + "Nobody" : ChatColor.YELLOW + "       Second place:" + ChatColor.GREEN + places[1] + ChatColor.YELLOW + ", " + ChatColor.GREEN + playerData.get(Bukkit.getPlayer(places[1])).getKills() + ChatColor.YELLOW + " kills.");
				p.sendMessage((places[2] == null) ? ChatColor.YELLOW + "        Third place: " + ChatColor.GRAY + "Nobody" : ChatColor.YELLOW + "        Third place:" + ChatColor.GREEN + places[2] + ChatColor.YELLOW + ", " + ChatColor.GREEN + playerData.get(Bukkit.getPlayer(places[2])).getKills() + ChatColor.YELLOW + " kills.");
				p.sendMessage(" ");
				p.sendMessage(Messages.BORDER.getValue());
			} else if(mode.equals(GameModeState.DEF_AND_DET))
			{
				p.sendMessage(Messages.BORDER.getValue());
				p.sendMessage(" ");
				Team t = winningTeam();
				p.sendMessage(ChatColor.GRAY + "The " + t.getColor() + t.getValue() + ChatColor.GRAY + " team has won!");
				p.sendMessage(" ");
				p.sendMessage(Messages.BORDER.getValue());
			}
		}
	}

	private boolean outOfBounds(Ship s)
	{
		if(s == null)
			return false;
		Location loc = s.getLocation();
		if(loc == null)
			return false;
		int[] dims = worldData.get(currentData).getMapDims();
		return (loc.getX() > dims[0] || loc.getX() < dims[1] || loc.getY() > dims[2] || loc.getY() < dims[3] || loc.getZ() > dims[4] || loc.getZ() < dims[5]);
	}
	
	public int getAlivePlayers()
	{
		int i = 0;
		for(PlayerData pd : playerData.values())
			i++;
		return i;
	}
	
	private Team winningTeam()
	{
		Team t = Team.RED;
		if(Team.GREEN.getPoints() > t.getPoints())
			t = Team.GREEN;
		if(Team.YELLOW.getPoints() > t.getPoints())
			t = Team.YELLOW;
		if(Team.BLUE.getPoints() > t.getPoints())
			t = Team.BLUE;
		return t;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(sender.isOp() && sender instanceof Player)
			if(cmd.getName().equalsIgnoreCase("debug-test"))
			{
				Player p = (Player) sender;
				state = GameState.GAME_ACTIVE;
				mode = GameModeState.DEATHMATCH;
				Location loc = p.getLocation();
				PlayerData pd = new PlayerData(new CoordSet(loc.getX(), loc.getY(), loc.getZ()), null, p);
				playerData.put(p.getUniqueId(), pd);
				pd.setShip(ShipType.MARAUDER);
				ArmorStand as = pd.getShip().generateShipModel(p.getLocation());
				as.addPassenger(p);
			} else if(cmd.getName().equalsIgnoreCase("debug-ship"))
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
					s.saveSchematic(new File(pluginPath + "CapitalData" + File.separator + args[0] + ".schem"));
				} catch (IOException e) { e.printStackTrace(); }
			} else if(cmd.getName().equalsIgnoreCase("debug-active"))
				startStarjet();
			else if(cmd.getName().equalsIgnoreCase("debug-start"))
				startCountdown(10);
		return true;
	}
	
}
