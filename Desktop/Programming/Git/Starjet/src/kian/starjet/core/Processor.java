package kian.starjet.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
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

import kian.starjet.attacks.ModelAttack;
import kian.starjet.attacks.ParticleBeam;
import kian.starjet.attacks.ParticleProjectile;
import kian.starjet.attacks.ParticleSpell;
import kian.starjet.ship.AttackData;
import kian.starjet.ship.AttackType;
import kian.starjet.ship.CapitalShip;
import kian.starjet.ship.Ship;
import kian.starjet.ship.Turret;
import kian.starjet.util.Matrix;
import kian.starjet.util.Matrix.Coordinate;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Core class. Processes all weapon usage. Is regulated by external classes.
 * Altered version of the processor from tower defense. Is included in the plugin due
 * to complications involved in outsourcing it to external plugins.
 * @author TheKian
 */
public class Processor 
{
	
	private static List<Ship> targets = new ArrayList<Ship>(); //A list of valid targets
	private static List<ParticleBeam> beams = new ArrayList<ParticleBeam>(); //Buffer for particle beam attacks passed in by attackers
	private static List<ParticleProjectile> projectiles = new ArrayList<ParticleProjectile>(); //Buffer for particle projectiles and the class' descendents
	private static List<ModelAttack> models = new ArrayList<ModelAttack>(); //Buffer for 3D model-based attacks
	private static List<CapitalShip> dreadnoughts = new ArrayList<CapitalShip>();
	
	/***
	 * Clears all data from the processor, effectively resetting it to the way it was at initialization.
	 */
	public static void clearAll()
	{
		targets.clear();
		beams.clear();
		projectiles.clear();
	}
	
	/***
	 * Processes all special attack information, primarily AOE field and particle attacks.
	 * @param plugin - Bukkit plugin to handle runnable calls.
	 */
	public static void process(Plugin plugin)
	{
		List<ParticleProjectile> ptr = new ArrayList<ParticleProjectile>(); //Projectiles to remove. See above.
		List<ModelAttack> mtr = new ArrayList<ModelAttack>();
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
				for(Ship ship : targets)
				{
					if(ship.getWorld() != pb.getStartPoint().getWorld()) //Prevents beams from hitting mobs in a different world
						continue;
					//Do not damage owner or team
					if((pb.getOwner() != null && ship.equals(pb.getOwner())) || (pb.getOwner().getTeam() != null && pb.getOwner().getTeam().equals(ship.getTeam())))
						continue;		
					if(collides(loc, ship, pb.getSize())) //Beam collision check
					{
						b = true;
						if(pb.getOwner() != null)
							ship.damage(pb.getDamage(), pb.getOwner());
						else
							ship.damage(pb.getDamage());
						//Tell the server that the entity can immediately be damaged again.break;
					}
					if(b)
						break;
				}
				for(CapitalShip cs : dreadnoughts)
				{
					if(cs.getLocation().getWorld() != pb.getStartPoint().getWorld()) //Prevents beams from hitting mobs in a different world
						continue;
					boolean b2 = false;
					for(Turret t : cs.getTurrets())
						if(t.equals(pb.getOwner()))
						{
							b2 = true;
							break;
						}
					if(b2) //Prevent ship from killing itself
						continue;			
					if(collides(loc, cs, pb.getSize())) //Beam collision check
					{
						b = true;
						if(pb.getOwner() != null)
							cs.damage(pb.getDamage(), pb.getOwner());
						else
							cs.damage(pb.getDamage());
						//Tell the server that the entity can immediately be damaged again.break;
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
			for(Ship ship : targets)
			{
				if(!ship.getWorld().equals(loc.getWorld()))
					continue;
				//Do not damage owner or team
				if((pp.getOwner() != null && ship.equals(pp.getOwner())) || (pp.getOwner().getTeam() != null && pp.getOwner().getTeam().equals(ship.getTeam())))
					continue;	
				if(collides(loc, ship, pp.getSize()))
				{
					double d = pp.getDamage(); //For code readability
					if(pp.getOwner() != null)
						ship.damage(d, pp.getOwner());
					else
						ship.damage(d);
						ptr.add(pp);
						b = true;
						break;
					}
			}
			for(CapitalShip cs : dreadnoughts)
			{
				if(cs.getLocation().getWorld() != pp.getLocation().getWorld()) //Prevents beams from hitting mobs in a different world
					continue;
				boolean b2 = false;
				for(Turret t : cs.getTurrets())
					if(t.equals(pp.getOwner()))
					{
						b2 = true;
						break;
					}
				if(b2) //Prevent ship from killing itself
					continue;			
				if(collides(loc, cs, pp.getSize())) //Beam collision check
				{
					b = true;
					if(pp.getOwner() != null)
						cs.damage(pp.getDamage(), pp.getOwner());
					else
						cs.damage(pp.getDamage());
					//Tell the server that the entity can immediately be damaged again.break;
				}
				if(b)
					break;
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
							ptr.add(pp);
							break;
						}
					}
				pp.move();
				if(pp.getAge() >= pp.getMaxAge())
					ptr.add(pp);
			}
		}
		
		//Clears buffer of expired particle projectiles.
		for(ParticleProjectile pp : ptr)
			projectiles.remove(pp);
		ptr.clear();
		
		for(ModelAttack ma : models)
		{
			ma.updateMovement();
			for(Ship s : targets)
			{
				if(s.getTeam() != null && s.getTeam().equals(ma.getOwner().getTeam()))
					continue;

				boolean b = false; //Indicator for whether the projectile has already expired
				Location loc = ma.getLocation();
				//Target check
				for(Ship ship : targets)
				{
					if(!ship.getWorld().equals(loc.getWorld()))
						continue;
					if((ma.getOwner() != null && ship.equals(ma.getOwner())) || (ma.getOwner().getTeam() != null && ma.getOwner().getTeam().equals(ship.getTeam())))
						continue;	
					if(collides(loc, ship, ma.getSize()))
					{
						double d = ma.getDamage(); //For code readability
						if(ma.getOwner() != null)
							ship.damage(d, ma.getOwner());
						else
							ship.damage(d);
							mtr.add(ma);
							b = true;
							break;
						}
				}
				for(CapitalShip cs : dreadnoughts)
				{
					if(cs.getLocation().getWorld() != ma.getModel().getWorld()) //Prevents beams from hitting mobs in a different world
						continue;
					boolean b2 = false;
					for(Turret t : cs.getTurrets())
						if(t.equals(ma.getOwner()))
						{
							b2 = true;
							break;
						}
					if(b2) //Prevent ship from killing itself
						continue;			
					if(collides(loc, cs, ma.getSize())) //Beam collision check
					{
						b = true;
						if(ma.getOwner() != null)
							cs.damage(ma.getDamage(), ma.getOwner());
						else
							cs.damage(ma.getDamage());
						//Tell the server that the entity can immediately be damaged again.break;
					}
					if(b)
						break;
				}
				if(b)
					continue;
				//Checks whether a projectile has collided with a block.
				//Unless collision is disabled
				BlockIterator iterator = new BlockIterator(ma.getLocation().getWorld(), loc.toVector(), ma.getDirection().normalize(), 0.0D, 1);
				Block bl = null;
				if(b == false)
					while (iterator.hasNext()) 
					{
						bl = iterator.next();
						if(!bl.getType().equals(Material.AIR) && bl.getType().isSolid()) //Checks whether collision is with a solid block
						{
							mtr.add(ma);
							break;
						}
					}
				ma.incrementAge();
				if(ma.getAge() >= ma.getMaxAge())
					mtr.add(ma);

			}
		}
		
		purgeTargets();
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
	
	public static void addModelAttack(ModelAttack ma)
	{
		models.add(ma);
	}
	
	/***
	 * Returns a list of all valid targets in range of the location parameter.
	 * @param attack - AttackData instance.
	 * @param loc - Location of the attack's starting point.
	 * @return List of new targets.
	 */
	public static List<Ship> getTargetsInRange(AttackData attack, Location loc)
	{
		List<Ship> newTargets = new ArrayList<Ship>();
		for(Ship s : targets)
			if(s.getLocation().distance(loc) <= attack.getRange())
				newTargets.add(s);
		return newTargets;
	}
	
	/***
	 * Moved out of tower class. Adds a new attack based on AttackData. Purpose: Centralizes all attack processing to processor.
	 * @param loc - Attack initial location
	 * @param attack - Attack's basic data
	 * @param owner - Player who is responsible for the attack
	 */
	public static void addAttack(Location loc, AttackData attack, Ship owner)
	{
		//Ship target = newTargets.get(id);
		//If attack is a particle beam
		Vector v = owner instanceof Turret ? ((Turret) owner).getLookAngle() : owner.getShipModel().getVelocity().clone().normalize();
		if(attack.getAttackType().equals(AttackType.BEAM))
			addParticleBeam(new ParticleBeam(loc, attack.getData1(), v.clone(), attack.getRange(), attack.getDamage(), 0.3F, 0, null/*Sound*/, owner, false));
		//If attack is a particle projectile
		else if(attack.getAttackType().equals(AttackType.PROJECTILE))
			addParticleProjectile(new ParticleProjectile(loc.getX(), loc.getY(), loc.getZ(), attack.getData1(), v.clone().multiply(attack.getSpeed()), loc.getWorld(), 20 * (int) (attack.getRange() / attack.getSpeed()), (float) attack.getSize(), attack.getDamage(), owner, null/*Sound*/, 0, owner instanceof Turret ? true : false));
		//If attack is a particle spell
		else if(attack.getAttackType().equals(AttackType.SPELL))
			addParticleProjectile(new ParticleSpell(loc.getX(), loc.getY(), loc.getZ(), attack.getData1(), v.clone().multiply(attack.getSpeed()), loc.getWorld(), 20 * (int) (attack.getRange() / attack.getSpeed()), (float) attack.getSize(), attack.getDamage(), owner, null/*Sound*/, 0, attack.getData2(), 0, (float) attack.getSize(), owner instanceof Turret ? true : false));	
		else if(attack.getAttackType().equals(AttackType.MODEL))
			addModelAttack(new ModelAttack(loc, attack.getDamage(), attack.getData3(), attack.getData4(), attack.getSpeed(), attack.getSize(), v.clone(), owner));
	}

	/***
	 * Custom method to find distance to approximately the midpoint of the entity. 
	 */
	private static boolean collides(Location loc, Ship ship, double size)
	{
		if(ship != null)
		{
			//Checks if the location is within bounds, plus margin of attack size, of the given ship
			return (loc.getY() < ship.getLocation().getY() + ship.getDimensions()[2] + size && 
					loc.getY() > ship.getLocation().getY() + ship.getDimensions()[3] - size &&
					loc.getX() < ship.getLocation().getX() + ship.getDimensions()[0] + size && 
					loc.getX() > ship.getLocation().getX() + ship.getDimensions()[1] - size &&
					loc.getZ() < ship.getLocation().getZ() + ship.getDimensions()[4] + size && 
					loc.getZ() > ship.getLocation().getZ() + ship.getDimensions()[5] - size);
		}
		return false;
	}
	
	/***
	 * Custom method to find distance to approximately the midpoint of the entity. 
	 */
	private static boolean collides(Location loc, CapitalShip ship, double size)
	{
		if(ship != null)
		{
			//Checks if the location is within bounds, plus margin of attack size, of the given ship
			return (loc.getY() < ship.getLocation().getY() + ship.getDimensions()[2] + size && 
					loc.getY() > ship.getLocation().getY() + ship.getDimensions()[3] - size &&
					loc.getX() < ship.getLocation().getX() + ship.getDimensions()[0] + size && 
					loc.getX() > ship.getLocation().getX() + ship.getDimensions()[1] - size &&
					loc.getZ() < ship.getLocation().getZ() + ship.getDimensions()[4] + size && 
					loc.getZ() > ship.getLocation().getZ() + ship.getDimensions()[5] - size);
		}
		return false;
	}
	
	//Due to the intensive operations involved, however, it is best that this simply not be used while testing.
	/***
	 * Exceedingly complicated collision detection method which checks whether a point is located within an arbritrarily rotated cuboid.
	 * The point given is converted into the space defined by axes defined based on the velocity vector of the ship, centered on the Ship's
	 * location. The point is then checked in the relatively non-rotated cuboid defined in the new coordinates.
	 * @param loc - Location of object to be checked for collision
	 * @param ship - Ship to be checked for collision
	 * @param size - Size of the object to be checked for collision
	 * @return
	 */
	private static boolean complexCollides(Location loc, Ship ship, double size)
	{
		if(ship != null)
		{
			Vector axisZ = ship.getShipModel().getVelocity().clone().normalize();
			Vector axisX = new Vector(-axisZ.getZ(), axisZ.getY(), axisZ.getX()).normalize();
			Vector axisY = axisZ.getCrossProduct(axisX);
			Vector p = loc.toVector().subtract(ship.getLocation().toVector());
			double[][] matrix = {{axisZ.getX(), axisX.getX(), axisY.getX(), p.getX()}, {axisZ.getY(), axisX.getY(), axisY.getY(), p.getY()}, {axisZ.getZ(), axisX.getZ(), axisY.getZ(), p.getZ()}};
			Matrix m = new Matrix(matrix);
			m.RREF();
			CoordSet newLoc = new CoordSet(m.getCoordinate(new Coordinate(1, 3)).doubleValue(), m.getCoordinate(new Coordinate(2, 3)).doubleValue(), m.getCoordinate(new Coordinate(0, 3)).doubleValue());
			return (newLoc.getY() < ship.getDimensions()[2] + size && 
					newLoc.getY() > ship.getDimensions()[3] - size &&
					newLoc.getX() < ship.getDimensions()[0] + size && 
					newLoc.getX() > ship.getDimensions()[1] - size &&
					newLoc.getZ() < ship.getDimensions()[4] + size && 
					newLoc.getZ() > ship.getDimensions()[5] - size);
		}
		return false;
	}

	/***
	 * Purges all dead targets in the processor.
	 */
	public static void purgeTargets()
	{
		//Go through and clear targets and enemies
		for(Iterator<Ship> iterator = targets.iterator(); iterator.hasNext(); )
		{
			Ship s = iterator.next();
			if(s == null || s.getHealth() <= 0)
				iterator.remove();
		}
	}
	
	/***
	 * If a living entity is contained in the target list, it will be removed.
	 * @param s - Ship to remove from targets.
	 */
	public static void removeTarget(Ship s)
	{
		if(targets.contains(s))
			targets.remove(s);
	}
	
	/***
	 * Returns list of valid targets in the processor.
	 * @return Target list.
	 */
	public static List<Ship> getTargets()
	{
		return targets;
	}
	
}