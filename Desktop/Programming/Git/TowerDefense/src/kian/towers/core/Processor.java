package kian.towers.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wither;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import kian.towers.attacks.AOEField;
import kian.towers.attacks.CustomProjectile;
import kian.towers.attacks.ParticleBeam;
import kian.towers.attacks.ParticleProjectile;
import kian.towers.attacks.ParticleTracing;
import kian.towers.enemy.MobData;
import kian.towers.tower.AttackData;
import kian.towers.tower.AttackType;
import kian.towers.tower.SplashData;
import kian.towers.tower.Tower;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Core class. Processes all weapon usage. Is regulated by external classes.
 * @author TheKian
 */
public class Processor 
{
	
	private static List<LivingEntity> targets = new ArrayList<LivingEntity>(); //A list of valid targets 
	private static List<ParticleBeam> beams = new ArrayList<ParticleBeam>(); //Buffer for particle beam attacks passed in by attackers
	private static List<ParticleProjectile> projectiles = new ArrayList<ParticleProjectile>(); //Buffer for particle projectiles and the class' descendents
	private static List<AOEField> aoes = new ArrayList<AOEField>(); //Buffer for AOE field attacks
	private static HashMap<Projectile, CustomProjectile> projs = new HashMap<Projectile, CustomProjectile>(); //Buffer for custom projectile data
	private static HashMap<LivingEntity, MobData> enemies = new HashMap<LivingEntity, MobData>(); //List of enemy data - targets do not need to be in this list, but any that are have level considered.
	
	/***
	 * Clears all data from the processor, effectively resetting it to the way it was at initialization.
	 */
	public static void clearAll()
	{
		targets.clear();
		beams.clear();
		projectiles.clear();
		aoes.clear();
		projs.clear();
		enemies.clear();
	}
	
	/***
	 * Processes all special attack information, primarily AOE field and particle attacks.
	 * @param plugin - Bukkit plugin to handle runnable calls.
	 */
	public static void process(Plugin plugin)
	{
		List<ParticleProjectile> ptr = new ArrayList<ParticleProjectile>(); //Projectiles to remove. See above.
		List<Projectile> ptr2 = new ArrayList<Projectile>();
		//Beam processing: Beams are immediately processed and don't stick around, preventing large amounts of beam data
		//from being kept around for any extended period.
		for(ParticleBeam pb : beams)
		{
			//Loc is the current location of the particle beam. Loc2 is used to determine the distance moved per jump.
			Location loc = new Location(pb.getStartPoint().getWorld(), pb.getStartPoint().getX(), pb.getStartPoint().getY(), pb.getStartPoint().getZ());
			Location loc2 = new Location(pb.getStartPoint().getWorld(), pb.getStartPoint().getX(), pb.getStartPoint().getY(), pb.getStartPoint().getZ());
			while(pb.getLength() < pb.getMaxLength())
			{
				boolean b = false; //b stores whether or not the beam has hit something yet
				loc.add(pb.getDirecton());
				//loc2 is used for convenient distance calculations with the beam's movement
				pb.addLength(loc.distance(loc2));
				//It is then set to the same location as loc to repeat the cycle
				loc2.setX(loc.getX());
				loc2.setY(loc.getY());
				loc2.setZ(loc.getZ());
				for(Player p : Bukkit.getOnlinePlayers()) //Draws the beam to all online players in the world
				{
					if(p.getWorld().equals(pb.getStartPoint().getWorld()))
					{
						pb.drawToPlayer(p, loc);
					}
				}
				//Cycles through target list
				for(LivingEntity le : targets)
				{
					if(le.getWorld() != pb.getStartPoint().getWorld()) //Prevents beams from hitting mobs in a different world
						continue;
					if(le instanceof Player && le.equals(pb.getOwner())) //For PvP usage
						continue;
					double d1 = 1; //d1 determines the sensitivity of the hitbox, which cannot be perfectly accurate
								  //without being independently set for each and every different mob
					if(le instanceof EnderDragon || le instanceof Wither || le instanceof Ghast) //Especially large mobs get their own values
						d1 = 3;
					else if(le instanceof Giant)
						d1 = 4.5;			
					if(customDistance(loc, le) < d1) //Beam collision check
					{
						b = true;
						//Applies splash data if it exists
						if(pb.getSplashData() != null) //SplashData passes new AOE field information to the processor
							pb.getSplashData().applySplash(loc, pb.getDamageLevel(), pb.getEffect(), plugin);
						double d = pb.getDamage();
						if(!(enemies.keySet().contains(le))) //Considers damage level if the enemies HashMap contains the entity
						{
							if(canDamage(enemies.get(le).getLevel(), pb.getDamageLevel()) || enemies.get(le).getLevel() == 5) //Only damages if the attack is able to damage it
							{
								//if(pb.getOwner() != null) //Tells the server who damaged it if its owner exists
								//	le.damage(enemies.get(le).getLevel() == 5 ? d/2 : d, pb.getOwner());
								//else
									le.damage(enemies.get(le).getLevel() == 5 ? d/2 : d);
							}
						} else //Otherwise, applies damage normally
						{
							//if(pb.getOwner() != null)
							//	le.damage(d, pb.getOwner());
							//else
								le.damage(d);
						}
						//Tell the server that the entity can immediately be damaged again.
						le.setNoDamageTicks(0);
						le.setLastDamage(0);
						break;
					}
					if(b)
						break;
				}
				if(b)
					break;
				//BlockIterator checks whether the beam has hit a block
				//But only if collision is enabled
				if(pb.getCollision())
				{
					BlockIterator iterator = new BlockIterator(pb.getStartPoint().getWorld(), loc.toVector(), pb.getDirecton().normalize(), 0.0D, 1);
					Block bl = null;
					while (iterator.hasNext()) 
					{
						bl = iterator.next();
						//Stops the beam if it hits a solid block
						if(!bl.getType().equals(Material.AIR) && bl.getType().isSolid())
						{
							//Applies splash data if it exists
							if(pb.getSplashData() != null)
								pb.getSplashData().applySplash(loc, pb.getDamageLevel(), pb.getEffect(), plugin);
							b = true;
							break;
						}
					}
					if(b)
						break;
				}
			}
		}
		
		//Clear of all beams, since they are immediately and instantly processed
		beams.clear();
		//Processes all particle projectiles. These expire after a certain amount of time or when they collide with something.
		for(ParticleProjectile pp : projectiles)
		{
			for(Player p : Bukkit.getOnlinePlayers()) //Draws projectile to all online players in the same world
				if(p.getWorld().equals(pp.getLocation().getWorld()))
					pp.drawToPlayer(p);
			boolean b = false; //Indicator for whether the projectile has already expired
			Location loc = pp.getLocation();
			//Target check
			for(LivingEntity le : targets)
			{
				if(!le.getWorld().equals(loc.getWorld()))
					continue;
					double d1 = 1;
					if(le instanceof EnderDragon || le instanceof Wither || le instanceof Ghast) //Hitbox sensitivity adjustment
						d1 = 3;
					else if(le instanceof Giant)
						d1 = 4.5;
					if(customDistance(loc, le) < d1)
					{
						if(pp.getSplash() != null) //If splash data exists, activates it
							pp.getSplash().applySplash(loc, pp.getDamageLevel(), pp.getEffect(), plugin);
						double d = pp.getDamage(); //For code readability
						if(!(enemies.keySet().contains(le))) //Considers damage level if the enemies HashMap contains the entity
						{
							if(canDamage(enemies.get(le).getLevel(), pp.getDamageLevel()) || enemies.get(le).getLevel() == 5) //Only damages if the attack is able to damage it
							{
								//if(pp.getOwner() != null) //Tells the server who damaged it if its owner exists
								//	le.damage(enemies.get(le).getLevel() == 5 ? d/2 : d, pp.getOwner());
								//else
									le.damage(enemies.get(le).getLevel() == 5 ? d/2 : d);
							}
						} else //Otherwise, applies damage normally
						{
							//if(pp.getOwner() != null)
							//	le.damage(d, pp.getOwner());
							//else
								le.damage(d);
						}
							//Allow target to immediately take damage again
							le.setLastDamage(0);
							le.setNoDamageTicks(0);
						ptr.add(pp);
						b = true;
						break;
					}
			}
			if(b)
				continue;
			//Checks whether a projectile has collided with a block.
			//Unless collision is disabled
			if(pp.getCollision())
			{
				BlockIterator iterator = new BlockIterator(pp.getLocation().getWorld(), loc.toVector(), pp.getVector().normalize(), 0.0D, 1);
				Block bl = null;
				if(b == false)
					while (iterator.hasNext()) 
					{
						bl = iterator.next();
						if(!bl.getType().equals(Material.AIR) && bl.getType().isSolid()) //Checks whether collision is with a solid block
						{
							if(pp.getSplash() != null)
								pp.getSplash().applySplash(loc, pp.getDamageLevel(), pp.getEffect(), plugin);
							ptr.add(pp);
							break;
						}
					}
			}
			pp.move();
			if(pp.getAge() >= pp.getMaxAge())
				ptr.add(pp);
		}
		
		//Clears buffer of expired particle projectiles.
		for(ParticleProjectile pp : ptr)
			projectiles.remove(pp);
		ptr.clear();
		
		//Due to problems with the EntityDamageByEntityEvent
		for(Projectile proj : projs.keySet())
		{
			Location loc = proj.getLocation();
			//Target check
			for(LivingEntity le : targets)
			{
				if(!le.getWorld().equals(loc.getWorld()))
					continue;
					double d1 = 1;
					if(le instanceof EnderDragon || le instanceof Wither || le instanceof Ghast) //Hitbox sensitivity adjustment
						d1 = 3;
					else if(le instanceof Giant)
						d1 = 4.5;
					if(customDistance(loc, le) < d1)
					{
						processProjectile(proj, le, plugin, false);
						ptr2.add(proj);
						break;
					}
			}
		}
		
		for(Projectile p : ptr2)
		{
			projs.remove(p);
			p.remove();
		}
		
		ptr2.clear();
		
		purgeTargets(); //Need to purge targets in case some were killed by beams or projectiles, which can add additional splash effects.
		
		//Processes all AOE field data.
		for(Iterator<AOEField> iterator = aoes.iterator(); iterator.hasNext();)
		{
			AOEField af = iterator.next();
			for(Player p : Bukkit.getOnlinePlayers()) //Draws field to all online players in the same world
				if(p.getWorld().equals(af.getCenter().getWorld()))
					af.drawToPlayer(p);
			for(LivingEntity le : targets)
			{
				if(af.getCenter().distance(le.getLocation()) <= af.getRadius()) //For convenience's sake, uses Location.distance(). 
				{																//AOEField should be on the same Y level as its intended targets.
					af.damage(le); //Uses AOEField method to save space
					if(af.getSplashData() != null) //Applies a separate splash to each affected target
						af.getSplashData().applySplash(le.getEyeLocation(), af.getDamageLevel(), af.getEffect(), plugin);
				}
			}
		}
		aoes.clear(); //All AOEs are immediately processed, and can be simply cleared.
		purgeProjectiles(); //Clear out any dead projectiles if it's necessary
		purgeTargets();
	}
	
	//Handles assigning tower attacks for the main plugin. Iterates through all towers in player data
	public static void processTowerAttacks(Iterable<PlayerData> playerData, Plugin plugin)
	{
		for(PlayerData pd : playerData)
			for(Tower t : pd.getTowers())
			{
				t.incrementCooldown();
				t.attack(targets, plugin);
			}
	}
	
	/***
	 * Adds a particle beam to the processor's buffer.
	 * @param pb - ParticleBeam to be passed
	 */
	public static void addParticleBeam(ParticleBeam pb)
	{
		beams.add(pb);
	}
	
	/***
	 * Adds a particle projectile to the processor's buffer.
	 * @param pp - ParticleProjectile to be passed
	 */
	public static void addParticleProjectile(ParticleProjectile pp)
	{
		projectiles.add(pp);
	}
	
	/***
	 * Adds an AOE field to the processor's buffer.
	 * @param af - AOEField to be passed
	 */
	public static void addAOEField(AOEField af)
	{
		aoes.add(af);
	}
	
	/***
	 * Adds projectile data to the processor's buffer.
	 * @param p - Projectile key.
	 * @param cp - CustomProjectile vlaue.
	 */
	public static void putProjectile(Projectile p, CustomProjectile cp)
	{
		projs.put(p, cp);
	}
	
	/***
	 * Processes the effects of a projectile impact.
	 * @param p - Projectile which has impacted a target
	 * @param le - Target to be affected
	 * @return Boolean: whether arguments are valid.
	 */
	public static boolean processProjectile(Projectile p, LivingEntity le, Plugin plugin, boolean b)
	{
		if(!projs.containsKey(p))
			return false;
		CustomProjectile cp = projs.get(p);
		double d = cp.getDamage();
		if(!(enemies.keySet().contains(le))) //Considers damage level if the enemies HashMap contains the entity
		{
			if(canDamage(enemies.get(le).getLevel(), cp.getDamageLevel()) || enemies.get(le).getLevel() == 5) //Only damages if the attack is able to damage it
			{
				//if(cp.getOwner() != null) //Tells the server who damaged it if its owner exists
				//	le.damage(enemies.get(le).getLevel() == 5 ? d/2 : d, cp.getOwner());
				//else
					le.damage(enemies.get(le).getLevel() == 5 ? d/2 : d);
			}
		} else //Otherwise, applies damage normally
		{
			//if(cp.getOwner() != null)
			//	le.damage(d, cp.getOwner());
			//else
				le.damage(d);
		}
		le.setLastDamage(0);
		le.setNoDamageTicks(0);
		if(cp.getSplash() != null)
			cp.getSplash().applySplash(p.getLocation(), cp.getDamageLevel(), ParticleTypeEnum.CRIT, plugin);
		if(cp.getSpecial() != null)
		{
			if(Math.random() < cp.getSpecialChance())
			{
				addAttack(p.getLocation(), cp.getSpecial(), cp.getOwner(), getTargetsInRange(cp.getSpecial(), p.getLocation()), 0);
			}
		}
		if(b)
			projs.remove(p);
		return true;
	}
	
	//If there are, for any reason, projectiles removed that are in the custom projectiles list, remove them from it
	private static void purgeProjectiles()
	{
		for(Iterator<Projectile> iterator = projs.keySet().iterator(); iterator.hasNext(); )
		{
			Projectile p = iterator.next();
			if(p == null || p.isDead())
				iterator.remove();
		}
	}
	
	/***
	 * Returns a list of all valid targets in range of the location parameter.
	 * @param attack - AttackData instance.
	 * @param loc - Location of the attack's starting point.
	 * @return List of new targets.
	 */
	public static List<LivingEntity> getTargetsInRange(AttackData attack, Location loc)
	{
		List<LivingEntity> newTargets = new ArrayList<LivingEntity>();
		for(LivingEntity le : targets)
			if(le.getLocation().distance(loc) <= attack.getRange())
				newTargets.add(le);
		return newTargets;
	}
	
	/***
	 * Moved out of tower class. Adds a new attack based on AttackData. Purpose: Centralizes all attack processing to processor.
	 * @param loc - Attack initial location
	 * @param attack - Attack's basic data
	 * @param owner - Player who is responsible for the attack
	 * @param newTargets - List of targets in range of attack
	 * @param id - ID assigned to primary entity target by the tower
	 */
	public static void addAttack(Location loc, AttackData attack, Player owner, List<LivingEntity> newTargets, int id)
	{
/*
		#################################################################################
		#	Explanation:																#
		#		The tower automatically attacks once and, if it has more attacks, 		#
		#		launches additional attacks at any other valid targets in the order 	#
		#		that they appear in the target list. Each attack adds appropriate attack#
		#		data to the processor buffers in accordance with AttackData.attackType.	#
		#################################################################################
*/		
		//If attack is a particle beam
		if(attack.getAttackType().equals(AttackType.PARTICLE_BEAM))
		{
			//new Location(loc.getWorld(), loc.getX(), loc.getY() + schem.getDimensions()[1] + 1, loc.getZ())
			addParticleBeam(new ParticleBeam(loc, attack.getParticle(), 200, attack.getDamage(), 0.05F, 0, null/*Sound*/, owner, attack.getSpecialChance(), attack.getSplashData(), newTargets.get(Math.min(id, newTargets.size() - 1)), false));
			if(attack.getAttacks() > 1)
			{
				id = 0;
				for(int i = 0; i < attack.getAttacks() - 1; i++)
				{
					addParticleBeam(new ParticleBeam(loc, attack.getParticle(), 200, attack.getDamage(), 0.05F, 0, null/*Sound*/, owner, attack.getSpecialChance(), attack.getSplashData(), newTargets.get(Math.min(id, newTargets.size() - 1)), false));
					id++;
					if(id >= newTargets.size())
						id = 0;
				}
			} //If attack is a particle projectile, creates a tracing particle projectile
		} else if(attack.getAttackType().equals(AttackType.PARTICLE_SHOOTER)) 
		{
			addParticleProjectile(new ParticleTracing(loc.getX(), loc.getY(), loc.getZ(), attack.getParticle(), new Vector(0, 10, 0), loc.getWorld(), 400, 0.1F, attack.getDamage(), attack.getDamageLevel(), owner, null/*Sound*/, 0, newTargets.get(Math.min(id, newTargets.size() - 1)), null, attack.getSplashData(), false));
			if(attack.getAttacks() > 1)
			{
				id = 0;
				for(int i = 0; i < attack.getAttacks() - 1; i++)
				{
					addParticleProjectile(new ParticleTracing(loc.getX(), loc.getY(), loc.getZ(), attack.getParticle(), new Vector(0, 10, 0), loc.getWorld(), 400, 0.1F, attack.getDamage(), attack.getDamageLevel(), owner, null/*Sound*/, 0, newTargets.get(Math.min(id, newTargets.size() - 1)), null, attack.getSplashData(), false));
					id++;
					if(id >= newTargets.size())
						id = 0;
				}
			} //If attack is an AOE field, adds it to the processor
		} else if(attack.getAttackType().equals(AttackType.AOE_FIELD)) 
		{
			addAOEField(new AOEField(false, attack.getRange(), attack.getDamage(), attack.getDamageLevel(), attack.getParticle(), loc, attack.getSplashData()));
			if(attack.getAttacks() > 1)
			{
				id = 0;
				for(int i = 0; i < attack.getAttacks() - 1; i++)
				{
					addAOEField(new AOEField(false, attack.getRange(), attack.getDamage(), attack.getDamageLevel(), attack.getParticle(), loc, attack.getSplashData()));
					id++;
					if(id >= newTargets.size())
						id = 0;
				}
			} //If attack fires a projectile, creates projectile and sets its data
		} else if(attack.getAttackType().equals(AttackType.PROJECTILE_SHOOTER))
		{
			Projectile p;
			if(attack.getProjectileType().equals(EntityType.ARROW))
				p = loc.getWorld().spawnArrow(loc, targetVector(loc, newTargets.get(Math.min(id, newTargets.size() - 1)).getEyeLocation()), 5, 0);
			else
			{
				p = (Projectile) loc.getWorld().spawnEntity(loc, attack.getProjectileType());
				p.setVelocity(targetVector(loc, newTargets.get(Math.min(id, newTargets.size() - 1)).getEyeLocation()).multiply(5));
			}
			putProjectile(p, new CustomProjectile(attack.getDamage(), attack.getDamageLevel(), attack.getSplashData(), attack.getSpecial(), attack.getSpecialChance(), owner));
			if(attack.getAttacks() > 1)
			{
				id = 0;
				for(int i = 0; i < attack.getAttacks() - 1; i++)
				{
					Projectile p2;
					if(attack.getProjectileType().equals(EntityType.ARROW))
						p2 = loc.getWorld().spawnArrow(loc, targetVector(loc, newTargets.get(Math.min(id, newTargets.size() - 1)).getEyeLocation()), 5, 0);
					else
					{
						p2 = (Projectile) loc.getWorld().spawnEntity(loc, attack.getProjectileType());
						p2.setVelocity(targetVector(loc, newTargets.get(Math.min(id, newTargets.size() - 1)).getEyeLocation()).multiply(5));
					}
					putProjectile(p2, new CustomProjectile(attack.getDamage(), attack.getDamageLevel(), attack.getSplashData(), attack.getSpecial(), attack.getSpecialChance(), owner));
					id++;
					if(id >= newTargets.size())
						id = 0;
				}
			}
			
		}
	}

	/***
	 * Custom method to find distance to approximately the midpoint of the entity. 
	 */
	private static double customDistance(Location loc, LivingEntity le)
	{
		if(le != null)
		{
			return loc.distance(new Location(loc.getWorld(), le.getLocation().getX(), le.getEyeHeight() * 2 / 3 + le.getLocation().getY() + 0.1, le.getLocation().getZ()));
		}
		return 0;
	}
	
	/***
	 * Returns vector in the direction of a given target location. For projectile entities.
	 */
	private static Vector targetVector(Location loc1, Location loc2)
	{
		return new Vector(loc2.getX() - loc1.getX(), loc2.getY() - loc1.getY(), loc2.getZ() - loc1.getZ()).normalize();
	}
	
	/***
	 * Purges all dead targets in the processor.
	 */
	public static void purgeTargets()
	{
		//Go through and clear targets and enemies
		for(Iterator<LivingEntity> iterator = targets.iterator(); iterator.hasNext(); )
		{
			LivingEntity le = iterator.next();
			if(le == null || le.getHealth() <= 0)
				iterator.remove();
		}
		for(Iterator<LivingEntity> iterator = enemies.keySet().iterator(); iterator.hasNext(); )
		{
			LivingEntity le = iterator.next();
			if(le == null || le.getHealth() <= 0)
				iterator.remove();
		}
	}
	
	/***
	 * If a living entity is contained in the target list, it will be removed.
	 * @param le - LivingEntity to remove
	 */
	public static void removeTarget(LivingEntity le)
	{
		if(targets.contains(le))
			targets.remove(le);
	}
	
	/***
	 * Returns whether a given attack level is able to damage a particular enemy.
	 * @param mobLevel - Level of the entity being damaged.
	 * @param attackLevel - Damage level of the attack.
	 * @return Whether attack can damage the entity.
	 */
	private static boolean canDamage(int mobLevel, int attackLevel)
	{
		switch(mobLevel)
		{
			case 2:
				return (attackLevel == 2 || attackLevel >= 4);
			case 3:
				return attackLevel >= 3;
			case 4:
				return attackLevel >= 4;
			case 5:
				return attackLevel == 5;
			default:
				return true;
		}	
	}
	
	/***
	 * Recreates projectile if it has hit the ground so it can pass through.
	 * @param p
	 * @param v
	 */
	public static void remakeProjectile(Projectile p, Vector v)
	{
		if(projs.containsKey(p))
		{
			CustomProjectile data = projs.get(p);
			Location loc = p.getLocation();
			Projectile newProjectile;
			if(p.getType().equals(EntityType.ARROW))
				newProjectile = loc.getWorld().spawnArrow(loc, v.normalize(), 5, 0);
			else
			{
				newProjectile = (Projectile) loc.getWorld().spawnEntity(loc, p.getType());
				newProjectile.setVelocity(v);
			}
			projs.remove(p);
			p.remove();
			projs.put(newProjectile, data);
		}
	}
	
	/***
	 * Returns list of valid targets in the processor.
	 * @return Target list.
	 */
	public static List<LivingEntity> getTargets()
	{
		return targets;
	}
	
	/***
	 * Returns HashMap containing MobData for select LivingEntities.
	 * @return - Enemy MobData HashMap.
	 */
	public static HashMap<LivingEntity, MobData> getEnemies()
	{
		return enemies;
	}
}