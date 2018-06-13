package kian.starjet.attacks;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import kian.starjet.ship.Ship;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Altered form of particle projectile that changes direction to follow its target. Avoids issues of calculating attack angles for moving targets.
 */
public class ParticleTracing extends ParticleProjectile 
{
	
	private Ship target;
	private Location targetLocation;
	
	public ParticleTracing(double xp, double yp, double zp, ParticleTypeEnum effect, Vector vector, World world, int maxAge, float size, double damage, Ship owner, Sound sound, int data, Ship target, Location targetLocation, boolean collision)
	{
		super(xp, yp, zp, effect, vector, world, maxAge, size, damage, owner, sound, data, collision);
		this.target = target;
		this.targetLocation = targetLocation;
	}
	
	public void setTarget(Ship s)
	{
		target = s;
	}
	
	public void setTargetLocation(Location l)
	{
		targetLocation = l;
	}
	
	public Ship getTarget()
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
			targetLocation = target.getLocation();
		this.setVector(calcVect());
	}
	
	/***
	 * Causes projectile to recalibrate direction if it is not heading toward its target. Makes a gradual turn, but is faster than most targets can move.
	 */
	private Vector calcVect()
	{
		double mp = this.getVector().length();
		Vector v = (new Vector(targetLocation.getX() - this.getLocation().getX(), targetLocation.getY() - this.getLocation().getY(), targetLocation.getZ() - this.getLocation().getZ())).normalize().multiply(mp);
		Vector cv = this.getVector();
		//~~~
		if(Math.abs(v.getX() - cv.getX()) <= 0.06)
			v.setX(cv.getX());
		if(Math.abs(v.getY() - cv.getY()) <= 0.06)
			v.setY(cv.getY());
		if(Math.abs(v.getZ() - cv.getZ()) <= 0.06)
			v.setZ(cv.getZ());
		//~~~
		if(v.getX() > cv.getX())
			v.setX(v.getX() - 0.05);
		else
			v.setX(v.getX() + 0.05);
		//~~~
		if(v.getY() > cv.getY())
			v.setY(v.getY() - 0.05);
		else
			v.setY(v.getY() + 0.05);
		//~~~
		if(v.getZ() > cv.getZ())
			v.setZ(v.getZ() - 0.05);
		else
			v.setZ(v.getZ() + 0.05);
		//~~~
		return v.normalize().multiply(mp);
	}
}

