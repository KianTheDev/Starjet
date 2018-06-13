package kian.towers.attacks;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import kian.towers.tower.SplashData;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Altered form of particle projectile that changes direction to follow its target. Avoids issues of calculating attack angles for moving targets.
 */
public class ParticleTracing extends ParticleProjectile 
{
	
	private LivingEntity target;
	private Location targetLocation;
	
	public ParticleTracing(double xp, double yp, double zp, ParticleTypeEnum effect, Vector vector, World world, int maxAge, float size, double damage, int damageLevel, LivingEntity owner, Sound sound, int data, LivingEntity target, Location targetLocation, SplashData splashData, boolean collision)
	{
		super(xp, yp, zp, effect, vector, world, maxAge, size, damage, damageLevel, owner, sound, data, splashData, collision);
		this.target = target;
		this.targetLocation = targetLocation;
	}
	
	public void setTarget(LivingEntity le)
	{
		target = le;
	}
	
	public void setTargetLocation(Location l)
	{
		targetLocation = l;
	}
	
	public LivingEntity getTarget()
	{
		return target;
	}
	
	public Location getTargetLocation()
	{
		return targetLocation;
	}
	
	@Override
	public void move() //Altered move() method; changes direction to follow enemies
	{
		super.move();
		if(target != null)
			targetLocation = target.getEyeLocation();
		Vector v = calcVect();
		this.setVector(v);
		//this.setVector(calcVect());
	}
	
	/***
	 * Causes projectile to recalibrate direction if it is not heading toward its target. Makes a gradual turn, but is faster than most targets can move.
	 */
	private Vector calcVect()
	{
		double mp = this.getVector().length();
		Vector targetVector = new Vector(targetLocation.getX() - this.getLocation().getX(), targetLocation.getY() - this.getLocation().getY(), targetLocation.getZ() - this.getLocation().getZ()).normalize().multiply(mp);
		Vector newVector = this.getVector().clone();
		
		//Checks if it's 'close enough'.
		if(Math.abs(newVector.getX() - targetVector.getX()) <= 1)
			newVector.setX(targetVector.getX());
		if(Math.abs(newVector.getY() - targetVector.getY()) <= 1)
			newVector.setY(targetVector.getY());
		if(Math.abs(newVector.getZ() - targetVector.getZ()) <= 1)
			newVector.setZ(targetVector.getZ());
		
		//Otherwise adjusts it
		if(newVector.getX() > targetVector.getX())
			newVector.setX(newVector.getX() - 0.99);
		else if(newVector.getX() < targetVector.getX())
			newVector.setX(newVector.getX() + 0.99);
		if(newVector.getY() > targetVector.getY())
			newVector.setY(newVector.getY() - 0.99);
		else if(newVector.getY() < targetVector.getY())
			newVector.setY(newVector.getY() + 0.99);
		if(newVector.getZ() > targetVector.getZ())
			newVector.setZ(newVector.getZ() - 0.99);
		else if(newVector.getZ() < targetVector.getZ())
			newVector.setZ(newVector.getZ() + 0.99);

		return newVector.normalize().multiply(mp);
	}
}

